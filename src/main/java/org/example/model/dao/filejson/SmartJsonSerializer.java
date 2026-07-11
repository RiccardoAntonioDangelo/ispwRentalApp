package org.example.model.dao.filejson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.entity.LazyEntityList;
import org.example.model.entity.actors.strategy.RoleStrategyI;
import org.example.model.services.CollectionI;
import org.example.model.services.EntityI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmartJsonSerializer {

    private static final String TYPE_KEY = "_type";
    private static final String ID_KEY = "_id";
    private final ObjectMapper mapper;
    private final Path baseDataFolder;

    private final ThreadLocal<Set<String>> processingRegistry = ThreadLocal.withInitial(HashSet::new);
    private final Map<Class<?>, String> classAliases = new HashMap<>();

    public SmartJsonSerializer(Path baseDataFolder) {
        this.baseDataFolder = baseDataFolder;
        this.mapper = new ObjectMapper();

        // Configurazione per la gestione nativa di LocalDate tramite Jackson
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void registerAlias(Class<?> clazz, String alias) {
        this.classAliases.put(clazz, alias.toLowerCase());
    }

    public synchronized void set(EntityI<?> entity) throws IOException {
        if (entity == null || entity.getId() == null) return;
        this.setId(entity, entity.getId().toString());
    }

    public synchronized void setId(EntityI<?> entity, String id) throws IOException {
        if (entity == null || id == null) return;

        Class<?> executionClass = entity.getClass();

        File targetFile = getFileForEntity(executionClass, id);
        if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        String entityKey = executionClass.getSimpleName() + "_" + id;
        Set<String> currentProcessing = processingRegistry.get();

        if (currentProcessing.contains(entityKey)) return;

        boolean isRootCall = currentProcessing.isEmpty();
        currentProcessing.add(entityKey);

        try {
            Map<String, Object> entityMap = convertEntityToMap(entity);
            entityMap.put(TYPE_KEY, executionClass.getName());
            mapper.writeValue(targetFile, entityMap);
        } catch (IllegalAccessException e) {
            throw new IOException("Errore nella Reflection in scrittura", e);
        } finally {
            currentProcessing.remove(entityKey);
            if (isRootCall) processingRegistry.remove();
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(String id, Class<T> targetClass) throws IOException {
        File sourceFile = getFileForEntity(targetClass, id);
        if (!sourceFile.exists()) return null;

        return (T) getSmart(sourceFile, id, targetClass);
    }

    public synchronized Object getSmart(String folderAlias, String id, Class<?> fallbackClass) throws IOException {
        File sourceFile = baseDataFolder.resolve(folderAlias.toLowerCase()).resolve(id + ".json").toFile();
        if (!sourceFile.exists()) return null;
        return getSmart(sourceFile, id, fallbackClass);
    }

    private synchronized Object getSmart(File sourceFile, String id, Class<?> fallbackClass) throws IOException {
        Map<?, ?> rawMap = mapper.readValue(sourceFile, Map.class);
        if (!rawMap.containsKey(TYPE_KEY)) {
            throw new IOException("Il file JSON non contiene l'attributo di tipo '" + TYPE_KEY + "'");
        }

        String className = rawMap.get(TYPE_KEY).toString();
        try {
            Class<?> targetClass = Class.forName(className);

            if (fallbackClass != null && !fallbackClass.isAssignableFrom(targetClass)) {
                targetClass = fallbackClass;
            }

            return rebuildEntityFromFile(sourceFile, id, targetClass);
        } catch (ClassNotFoundException e) {
            if (fallbackClass != null) {
                return rebuildEntityFromFile(sourceFile, id, fallbackClass);
            }
            throw new IOException("Classe salvata nel JSON non trovata: " + className, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Object rebuildEntityFromFile(File sourceFile, String id, Class<?> targetClass) throws IOException {
        String entityKey = targetClass.getSimpleName() + "_" + id;
        Set<String> currentProcessing = processingRegistry.get();

        if (currentProcessing.contains(entityKey)) {
            return null;
        }

        boolean isRootCall = currentProcessing.isEmpty();
        currentProcessing.add(entityKey);

        try {
            Map<String, Object> rawMap = mapper.readValue(sourceFile, Map.class);

            Object entity;
            try {
                entity = targetClass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                var constructor = targetClass.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                Object[] args = new Object[constructor.getParameterCount()];
                entity = constructor.newInstance(args);
            }

            List<Field> fields = getAllFields(targetClass);
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();

                if (fieldName.equals(TYPE_KEY)) continue;

                Object jsonValue = rawMap.get(fieldName);
                if (jsonValue == null) {
                    field.set(entity, null);
                    continue;
                }

                // 1. GESTIONE POLIMORFICA INLINE (Strategie di Ruolo & Pattern State)
                boolean isPolymorphicInline = RoleStrategyI.class.isAssignableFrom(field.getType())
                        || field.getType().getName().contains("RentalState")
                        || fieldName.equalsIgnoreCase("role")
                        || fieldName.equalsIgnoreCase("state");

                if (isPolymorphicInline) {
                    if (jsonValue instanceof Map) {
                        Map<?, ?> inlineMap = (Map<?, ?>) jsonValue;
                        if (inlineMap.containsKey(TYPE_KEY)) {
                            String concreteClassName = inlineMap.get(TYPE_KEY).toString();
                            try {
                                Class<?> concreteClass = Class.forName(concreteClassName);
                                Object inlineInstance = concreteClass.getDeclaredConstructor().newInstance();

                                for (Field sField : getAllFields(concreteClass)) {
                                    sField.setAccessible(true);
                                    String sFieldName = sField.getName();
                                    if (inlineMap.containsKey(sFieldName) && !sFieldName.equals(TYPE_KEY)) {
                                        assignPrimitiveValue(inlineInstance, sField, inlineMap.get(sFieldName));
                                    }
                                }
                                field.set(entity, inlineInstance);
                            } catch (Exception ex) {
                                field.set(entity, null);
                            }
                        }
                    }
                }
                // 2. GESTIONE ENTITÀ DIRETTE (File separato)
                else if (EntityI.class.isAssignableFrom(field.getType())) {
                    String linkedId = jsonValue.toString();
                    Object linkedEntity = get(linkedId, (Class<EntityI<?>>) field.getType());
                    field.set(entity, linkedEntity);
                }
                // 3. GESTIONE DESERIALIZZAZIONE DELLE MAPPE (Es. lazyCollections)
                else if (Map.class.isAssignableFrom(field.getType()) && jsonValue instanceof Map<?, ?> jsonMap) {
                    Map<Object, Object> rebuiltMap = new HashMap<>();

                    Type genericType = field.getGenericType();
                    Class<?> valueGenericClass = Object.class;
                    if (genericType instanceof ParameterizedType pt && pt.getActualTypeArguments().length == 2) {
                        Type valType = pt.getActualTypeArguments()[1];
                        if (valType instanceof ParameterizedType innerPt) {
                            valueGenericClass = (Class<?>) innerPt.getRawType();
                        } else if (valType instanceof Class<?> cl) {
                            valueGenericClass = cl;
                        }
                    }

                    for (Map.Entry<?, ?> entry : jsonMap.entrySet()) {
                        Object mapValue = entry.getValue();

                        if (mapValue instanceof Collection<?> jsonCollection && Collection.class.isAssignableFrom(valueGenericClass)) {
                            // 🔧 FIX: non serve piu' extractTargetEntityClass qui: il tipo
                            // concreto di ogni elemento e' ora salvato dentro l'elemento stesso
                            // (vedi rebuildCollection), perche' con CollectionI<?> (wildcard)
                            // i generici non permettono di dedurlo dal campo.
                            rebuiltMap.put(entry.getKey(), rebuildCollection(jsonCollection, valueGenericClass));
                        } else if (mapValue instanceof String targetId && EntityI.class.isAssignableFrom(valueGenericClass)) {
                            rebuiltMap.put(entry.getKey(), get(targetId, (Class<EntityI<?>>) valueGenericClass));
                        } else {
                            rebuiltMap.put(entry.getKey(), mapValue);
                        }
                    }
                    field.set(entity, rebuiltMap);
                }
                // 4. GESTIONE DESERIALIZZAZIONE COLLEZIONI/LISTE DIRETTE
                else if (Collection.class.isAssignableFrom(field.getType()) && jsonValue instanceof Collection<?> jsonCollection) {
                    // 🔧 FIX: stesso discorso, usiamo il tipo dichiarato del campo come containerType.
                    field.set(entity, rebuildCollection(jsonCollection, field.getType()));
                }
                // 5. PRIMITIVI E ALTRI TIPI
                else {
                    if (rawMap.containsKey(fieldName)) {
                        assignPrimitiveValue(entity, field, jsonValue);
                    }
                }
            }

            return entity;

        } catch (Exception e) {
            throw new IOException("Errore critico durante la ricostruzione via Reflection dell'oggetto: " + targetClass.getName(), e);
        } finally {
            currentProcessing.remove(entityKey);
            if (isRootCall) processingRegistry.remove();
        }
    }

    /**
     * 🔧 FIX: istanzia il "contenitore" corretto in base al tipo dichiarato.
     * Se il tipo di destinazione è (o estende) CollectionI, usa LazyEntityList
     * invece di un ArrayList grezzo: altrimenti il cast a valle (es. in
     * Session.getCollection()) fallisce con ClassCastException, perche' un
     * ArrayList non e' un CollectionI.
     */
    private Collection<Object> instantiateContainer(Class<?> containerType) {
        if (containerType != null && CollectionI.class.isAssignableFrom(containerType)) {
            return new LazyEntityList();
        }
        return new ArrayList<>();
    }

    /**
     * 🔧 FIX: ricostruisce una collection leggendo il tipo concreto salvato
     * DENTRO ogni elemento (vedi processCollection), invece di dedurlo dai
     * generici del campo. Necessario perche' con wildcard come CollectionI<?>
     * i generici si perdono a runtime (type erasure) e non basta piu'
     * risalire a un unico "targetEntityClass" per l'intera collection.
     * Mantiene un fallback per il vecchio formato (solo ID stringa).
     */
    @SuppressWarnings("unchecked")
    private Collection<Object> rebuildCollection(Collection<?> jsonCollection, Class<?> containerType) throws IOException {
        Collection<Object> rebuilt = instantiateContainer(containerType);
        for (Object item : jsonCollection) {
            if (item instanceof Map<?, ?> refMap && refMap.containsKey(TYPE_KEY) && refMap.containsKey(ID_KEY)) {
                try {
                    Class<?> concreteClass = Class.forName(refMap.get(TYPE_KEY).toString());
                    Object entity = get(refMap.get(ID_KEY).toString(), (Class<EntityI<?>>) concreteClass);
                    if (entity != null) rebuilt.add(entity);
                } catch (ClassNotFoundException e) {
                    throw new IOException("Classe non trovata nella collection: " + refMap.get(TYPE_KEY), e);
                }
            } else {
                // elemento non-entità (es. String di una List<String> qualsiasi): lo teniamo cosi' com'e'
                rebuilt.add(item);
            }
        }
        return rebuilt;
    }

    private void assignPrimitiveValue(Object entity, Field field, Object jsonValue) throws IllegalAccessException {
        Class<?> type = field.getType();

        // Assegnazione e parsing esplicito per LocalDate
        if (type == LocalDate.class) {
            if (jsonValue instanceof String str) {
                field.set(entity, LocalDate.parse(str));
            } else if (jsonValue instanceof List<?> list && list.size() >= 3) {
                int y = ((Number) list.get(0)).intValue();
                int m = ((Number) list.get(1)).intValue();
                int d = ((Number) list.get(2)).intValue();
                field.set(entity, LocalDate.of(y, m, d));
            }
            return;
        }

        if (type.isEnum()) {
            String enumValueStr = jsonValue.toString();
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Object enumValue = Enum.valueOf((Class<Enum>) type, enumValueStr);
            field.set(entity, enumValue);
        }
        else if (type == int.class || type == Integer.class) {
            field.set(entity, ((Number) jsonValue).intValue());
        } else if (type == boolean.class || type == Boolean.class) {
            field.set(entity, jsonValue);
        } else if (type == long.class || type == Long.class) {
            field.set(entity, ((Number) jsonValue).longValue());
        } else if (type == double.class || type == Double.class) {
            field.set(entity, ((Number) jsonValue).doubleValue());
        } else {
            field.set(entity, jsonValue);
        }
    }

    private Map<String, Object> convertEntityToMap(EntityI<?> entity) throws IllegalAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        List<Field> fields = getAllFields(entity.getClass());

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = field.get(entity);

            if (fieldName.equals("observersList") || fieldName.equals("observers")) continue;

            if (fieldValue == null) {
                map.put(fieldName, null);
                continue;
            }

            boolean isPolymorphicInline = fieldValue instanceof RoleStrategyI
                    || fieldValue.getClass().getName().contains("RentalState")
                    || fieldName.equalsIgnoreCase("role")
                    || fieldName.equalsIgnoreCase("state");

            if (isPolymorphicInline) {
                Map<String, Object> inlineMap = new HashMap<>();
                inlineMap.put(TYPE_KEY, fieldValue.getClass().getName());

                for (Field sField : getAllFields(fieldValue.getClass())) {
                    sField.setAccessible(true);
                    String sFieldName = sField.getName();
                    if (!sFieldName.equals(TYPE_KEY) && !sFieldName.equals("observersList") && !sFieldName.equals("observers")) {
                        inlineMap.put(sFieldName, sField.get(fieldValue));
                    }
                }
                map.put(fieldName, inlineMap);
            }
            else if (fieldValue instanceof EntityI<?> entityI) {
                set(entityI);
                map.put(fieldName, entityI.getId().toString());
            }
            else if (fieldValue instanceof Map<?, ?> sourceMap) {
                Map<Object, Object> processedMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                    Object mapValue = entry.getValue();

                    if (mapValue instanceof Collection handledCollection) {
                        processedMap.put(entry.getKey(), processCollection(handledCollection));
                    } else if (mapValue instanceof EntityI<?> entityInMap) {
                        set(entityInMap);
                        processedMap.put(entry.getKey(), entityInMap.getId().toString());
                    } else {
                        processedMap.put(entry.getKey(), mapValue);
                    }
                }
                map.put(fieldName, processedMap);
            }
            else if (fieldValue instanceof Collection<?> sourceCollection) {
                map.put(fieldName, processCollection(sourceCollection));
            }
            else {
                map.put(fieldName, fieldValue);
            }
        }
        return map;
    }

    /**
     * 🔧 FIX: per ogni EntityI dentro una collection, salviamo tipo + id
     * (non solo l'id come stringa nuda). Serve perche' campi come
     * Map<String, CollectionI<?>> usano un wildcard: a runtime i generici
     * sono cancellati e in lettura non c'e' modo di risalire al tipo
     * concreto (es. RentI) partendo solo dal campo dichiarato.
     */
    private List<Object> processCollection(Collection<?> collection) throws IOException {
        List<Object> processedList = new ArrayList<>();
        for (Object item : collection) {
            if (item instanceof EntityI<?> entityInList) {
                set(entityInList);
                Map<String, String> ref = new HashMap<>();
                ref.put(TYPE_KEY, entityInList.getClass().getName());
                ref.put(ID_KEY, entityInList.getId().toString());
                processedList.add(ref);
            } else {
                processedList.add(item);
            }
        }
        return processedList;
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldsList = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fieldsList.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return fieldsList;
    }

    private File getFileForEntity(Class<?> clazz, String id) {
        String folderName = classAliases.getOrDefault(clazz, clazz.getSimpleName().toLowerCase());
        return baseDataFolder.resolve(folderName).resolve(id + ".json").toFile();
    }
}