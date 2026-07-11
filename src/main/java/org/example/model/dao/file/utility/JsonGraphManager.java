package org.example.model.dao.file.utility;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.example.exceptions.dao.file.PersistenceException;
import org.example.model.entity.LazyEntityList;
import org.example.model.services.EntityI;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class JsonGraphManager {

    private static final String REF_KEY    = "_ref";
    private static final String FOLDER_KEY = "_folder";
    private static final String TYPE_KEY   = "_type";

    private final TypeRegistry registry;
    private final ObjectMapper  fullMapper;
    private final ObjectMapper  refMapper;

    private final ThreadLocal<Boolean> writingRoot =
            ThreadLocal.withInitial(() -> false);

    private final ThreadLocal<Queue<Object>> pendingSaves =
            ThreadLocal.withInitial(ArrayDeque::new);

    private final ThreadLocal<Set<Object>> expanding =
            ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    public JsonGraphManager(TypeRegistry registry) {
        this.registry   = registry;
        this.fullMapper = buildBaseMapper();
        SimpleModule listModule = new SimpleModule();
        listModule.addDeserializer(LazyEntityList.class, new LazyEntityListDeserializer());
        this.fullMapper.registerModule(listModule);
        this.refMapper = buildRefMapper();
    }

    // ── Mapper builders ────────────────────────────────────────────────────────

    private static ObjectMapper buildBaseMapper() {
        VisibilityChecker<?> checker = VisibilityChecker.Std.defaultInstance()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
        ObjectMapper m = new ObjectMapper();
        m.setVisibility(checker);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return m;
    }

    private ObjectMapper buildRefMapper() {
        ObjectMapper m = buildBaseMapper();
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new BeanSerializerModifier() {
            @Override
            @SuppressWarnings("unchecked")
            public JsonSerializer<?> modifySerializer(SerializationConfig config,
                                                      BeanDescription beanDesc, JsonSerializer<?> serializer) {
                if (registry.isRegistered(beanDesc.getBeanClass()))
                    return new EntityRefSerializer((JsonSerializer<Object>) serializer);
                return serializer;
            }
        });
        m.registerModule(module);
        return m;
    }

    // ── API pubblica ───────────────────────────────────────────────────────────

    /**
     * Serializza root in un JsonNode via refMapper (gestisce i riferimenti),
     * poi inietta _type a livello radice prima di scrivere su disco.
     * Il tipo viene così memorizzato nel file dell'entità stessa,
     * non nel puntatore che la referenzia.
     */
    public void save(Object root, String relativePath) throws IOException {
        boolean isTopLevel = expanding.get().isEmpty();
        if (registry.isRegistered(root.getClass()))
            writingRoot.set(true);
        try {
            JsonNode tree = refMapper.valueToTree(root);
            if (tree.isObject())
                ((ObjectNode) tree).put(TYPE_KEY, root.getClass().getName());
            refMapper.writeValue(prepareFile(relativePath), tree);
            if (isTopLevel)
                flushPendingSaves();
        } finally {
            if (isTopLevel) {
                writingRoot.remove();
                expanding.remove();
                pendingSaves.remove();
            }
        }
    }

    public boolean delete(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isBlank()) return false;
        return Files.deleteIfExists(registry.resolvePath(relativePath));
    }

    public <T> T load(String relativePath, Class<T> targetType) throws IOException {
        File file = registry.resolvePath(relativePath).toFile();
        if (!file.exists()) return null;

        JsonNode raw = fullMapper.readTree(file);

        // Se il file contiene _type, usiamo il tipo concreto salvato.
        // Questo gestisce correttamente le sottoclassi (es. Client al posto di User).
        Class<?> actualType = targetType;
        if (raw.has(TYPE_KEY)) {
            String className = raw.get(TYPE_KEY).asText();
            try {
                Class<?> fileType = Class.forName(className);
                // Accettiamo il tipo del file solo se è compatibile con targetType
                if (targetType.isAssignableFrom(fileType))
                    actualType = fileType;
                else
                    throw new IOException(
                            "Type mismatch: file declares " + className +
                                    " but expected " + targetType.getName()
                    );
            } catch (ClassNotFoundException e) {
                throw new IOException("Cannot resolve type from file: " + className, e);
            }
        }

        JsonNode resolved = resolveRefs(raw);
        return targetType.cast(fullMapper.treeToValue(resolved, actualType));
    }

    // ── Salvataggi differiti ───────────────────────────────────────────────────

    private void flushPendingSaves() throws IOException {
        Queue<Object> queue = pendingSaves.get();
        while (!queue.isEmpty()) {
            Object     entity = queue.poll();
            EntityI<?> e      = (EntityI<?>) entity;
            if (e.getId() == null) continue;
            String folder = registry.folderOf(entity.getClass());
            Path   path   = registry.entityPath(folder, e.getId().toString());
            if (!Files.exists(path))
                save(entity, registry.entityPathStr(e));
        }
    }

    // ── Tree Walk (Deserializzazione) ──────────────────────────────────────────

    /**
     * Risolve i nodi {_ref, _folder} caricando il file dell'entità referenziata.
     * _type non viene propagato manualmente: è già scritto dentro il file dell'entità
     * da save(), quindi LazyEntityListDeserializer lo trova direttamente.
     */
    private JsonNode resolveRefs(JsonNode node) throws IOException {
        if (node.isObject() && node.has(REF_KEY) && node.has(FOLDER_KEY)) {
            String id     = node.get(REF_KEY).asText();
            String folder = node.get(FOLDER_KEY).asText();
            File   file   = registry.entityPath(folder, id).toFile();
            if (!file.exists())
                throw new IOException("Referenced entity not found: " + folder + "/" + id);
            return fullMapper.readTree(file);
        }

        if (node.isObject()) {
            ObjectNode out = fullMapper.createObjectNode();
            node.fields().forEachRemaining(e -> out.set(e.getKey(), wrapResolve(e.getValue())));
            return out;
        }

        if (node.isArray()) {
            ArrayNode out = fullMapper.createArrayNode();
            node.forEach(item -> out.add(wrapResolve(item)));
            return out;
        }

        return node;
    }

    private JsonNode wrapResolve(JsonNode node) {
        try { return resolveRefs(node); }
        catch (IOException e) { throw new PersistenceException("Failed to resolve ref: " + node, e); }
    }

    // ── EntityRefSerializer ────────────────────────────────────────────────────

    private class EntityRefSerializer extends JsonSerializer<Object> {

        private final JsonSerializer<Object> delegate;

        EntityRefSerializer(JsonSerializer<Object> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (Boolean.TRUE.equals(writingRoot.get())) {
                writingRoot.set(false);
                expandOrRef(value, gen, () -> delegate.serialize(value, gen, provider));
            } else {
                scheduleIfMissing(value);
                writeRef(value, gen);
            }
        }

        private void expandOrRef(Object value, JsonGenerator gen, SerializationTask task)
                throws IOException {
            Set<Object> inProgress = expanding.get();
            if (inProgress.contains(value)) {
                writeRef(value, gen);
                return;
            }
            inProgress.add(value);
            try {
                task.run();
            } finally {
                inProgress.remove(value);
            }
        }

        private void scheduleIfMissing(Object value) {
            if (!(value instanceof EntityI<?> entity) || entity.getId() == null) return;
            try {
                Path path = registry.entityPath(
                        registry.folderOf(value.getClass()),
                        entity.getId().toString());
                if (!Files.exists(path))
                    pendingSaves.get().add(value);
            } catch (Exception ignored) {
                //no
            }
        }

        // _type rimosso: viene scritto nel file dell'entità da save(), non nel puntatore
        private void writeRef(Object value, JsonGenerator gen) throws IOException {
            EntityI<?> entity = (EntityI<?>) value;
            Object     id     = entity.getId();
            gen.writeStartObject();
            gen.writeStringField(REF_KEY,    id != null ? id.toString() : "null");
            gen.writeStringField(FOLDER_KEY, registry.folderOf(value.getClass()));
            gen.writeEndObject();
        }
    }

    // ── LazyEntityListDeserializer ─────────────────────────────────────────────

    @SuppressWarnings({"unchecked", "rawtypes"})
    private class LazyEntityListDeserializer extends StdDeserializer<LazyEntityList<?>> {

        LazyEntityListDeserializer() {
            super(LazyEntityList.class);
        }

        @Override
        public LazyEntityList deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            JsonNode       node   = p.getCodec().readTree(p);
            LazyEntityList result = new LazyEntityList();

            JsonNode listNode = node.get("list");
            if (listNode == null || !listNode.isArray()) return result;

            for (JsonNode element : listNode)
                result.add((Serializable) deserializeElement(element));

            return result;
        }

        // _type è ora dentro il file dell'entità caricata, quindi dentro element
        private Object deserializeElement(JsonNode element) throws IOException {
            if (!element.isObject() || !element.has(TYPE_KEY))
                return fullMapper.treeToValue(element, Object.class);

            String typeName = element.get(TYPE_KEY).asText();
            try {
                Class<?>   type = Class.forName(typeName);
                ObjectNode copy = element.deepCopy();
                copy.remove(TYPE_KEY);
                return fullMapper.treeToValue(copy, type);
            } catch (ClassNotFoundException e) {
                throw new IOException("Cannot resolve element type: " + typeName, e);
            }
        }
    }

    // ── Utils ──────────────────────────────────────────────────────────────────

    @FunctionalInterface
    private interface SerializationTask {
        void run() throws IOException;
    }

    private File prepareFile(String relativePath) {
        Path path   = registry.resolvePath(relativePath);
        File file   = path.toFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        return file;
    }
}