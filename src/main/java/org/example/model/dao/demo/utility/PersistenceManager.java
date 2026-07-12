package org.example.model.dao.demo.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceManager {

    protected static final Map<Class<?>, Map<String, Object>> storage = new ConcurrentHashMap<>();

    public PersistenceManager() {
        // Chiamata delegata alla classe ausiliaria statica
    }

    protected Class<?> getStorageBucket(Class<?> clazz) {
        return clazz;
    }

    public void save(String id, Object entity) {
        if (entity == null || id == null) {
            return;
        }
        Class<?> bucket = getStorageBucket(entity.getClass());
        storage.computeIfAbsent(bucket, k -> new ConcurrentHashMap<>()).put(id, entity);
    }

    public boolean delete(Class<?> clazz, String id) {
        if (id == null) {
            return false;
        }
        Class<?> bucket = getStorageBucket(clazz);
        Map<String, Object> classMap = storage.get(bucket);
        return classMap != null && classMap.remove(id) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String id) {
        if (id == null) {
            return null;
        }
        Class<?> bucket = getStorageBucket(clazz);
        Map<String, Object> classMap = storage.get(bucket);
        return (classMap != null) ? (T) classMap.get(id) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> clazz) {
        Class<?> bucket = getStorageBucket(clazz);
        Map<String, Object> classMap = storage.get(bucket);

        if (classMap == null) {
            return new ArrayList<>();
        }

        List<T> results = new ArrayList<>();
        for (Object obj : classMap.values()) {
            if (clazz.isInstance(obj)) {
                results.add((T) obj);
            }
        }
        return results;
    }
}