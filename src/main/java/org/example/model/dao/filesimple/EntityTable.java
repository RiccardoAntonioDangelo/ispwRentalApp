package org.example.model.dao.filesimple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.model.services.EntityI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EntityTable<T extends EntityI<?>> {

    private static final String TYPE_KEY = "_type";
    private final String tableName;
    private final Path tablePath;
    private final ObjectMapper mapper;

    // Mappatura per la SCRITTURA: Nome Colonna -> Estrattore (Getter)
    private final Map<String, Function<T, Object>> columns = new LinkedHashMap<>();

    // Mappatura per la LETTURA: Nome Colonna -> Iniettore (Setter funzionale)
    private final Map<String, BiConsumer<T, JsonNode>> inputs = new LinkedHashMap<>();

    public EntityTable(String tableName, Path dataFolder, ObjectMapper mapper) {
        this.tableName = tableName.toLowerCase();
        this.tablePath = dataFolder.resolve(this.tableName);
        this.mapper = mapper;
    }

    /**
     * Configura una colonna in SCRITTURA (da Oggetto a JSON)
     */
    public EntityTable<T> mapColumn(String columnName, Function<T, Object> extractor) {
        columns.put(columnName, extractor);
        return this;
    }

    /**
     * Configura una colonna in LETTURA (da JSON a Oggetto)
     */
    public EntityTable<T> unmapColumn(String columnName, BiConsumer<T, JsonNode> injector) {
        inputs.put(columnName, injector);
        return this;
    }

    /**
     * SALVA (Upsert) - Zero cicli tradizionali
     */
    public void upsert(T entity) throws IOException {
        if (entity == null || entity.getId() == null) throw new IllegalArgumentException("ID nullo");

        ObjectNode row = mapper.createObjectNode();
        columns.forEach((col, extractor) -> row.set(col, mapper.valueToTree(extractor.apply(entity))));
        row.put(TYPE_KEY, entity.getClass().getName());

        File file = tablePath.resolve(entity.getId().toString() + ".json").toFile();
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, row);
    }

    /**
     * ESTRAE (Select) attributo per attributo senza reflection invasiva o annotazioni
     */
    public T select(String id, Class<T> targetClass) throws IOException {
        File file = tablePath.resolve(id + ".json").toFile();
        if (!file.exists()) return null;

        JsonNode rawRow = mapper.readTree(file);

        try {
            // 1. Istanziamo l'oggetto vuoto usando il costruttore di default
            T entity = targetClass.getDeclaredConstructor().newInstance();

            // 2. Popoliamo l'oggetto attributo per attributo usando il forEach della mappa dei Setter
            // Se il JSON ha la colonna definita nello schema, eseguiamo la lambda del Setter
            inputs.forEach((columnName, injector) -> {
                if (rawRow.has(columnName)) {
                    injector.accept(entity, rawRow.get(columnName));
                }
            });

            return entity;

        } catch (Exception e) {
            throw new IOException("Errore durante la ricostruzione dell'entità per l'ID: " + id, e);
        }
    }
}