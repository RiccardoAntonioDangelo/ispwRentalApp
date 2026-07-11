package org.example.model.dao.demo.utility;

public class PersistenceManagerProxy extends PersistenceManager {

    @Override
    protected Class<?> getStorageBucket(Class<?> clazz) {
        if (clazz == null || clazz.equals(Object.class)) return Object.class;

        // Se la classe è Client o Admin, risale a User
        Class<?> parent = clazz.getSuperclass();
        if (parent != null && !parent.equals(Object.class)) {
            return getStorageBucket(parent); // Risalita ricorsiva
        }
        return clazz;
    }
}