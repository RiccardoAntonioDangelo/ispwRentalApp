package org.example.util.singleton;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SingletonService {
    private SingletonService() {}


    private static final Map<Class<? extends SingletonI>, SingletonI> registry = new ConcurrentHashMap<>();

    /**
     * Registra manualmente un'istanza.
     * @throws IllegalStateException se la classe è già stata registrata.
     */
    public static <T extends SingletonI> void register(Class<T> clazz, T instance) {
        if (instance == null) return;

        SingletonI existing = registry.putIfAbsent(clazz, instance);

        if (existing != null) {
            throw new IllegalStateException("ERRORE CRITICO: Il Singleton per [" + clazz.getSimpleName() + "] è già stato inizializzato e non può essere sovrascritto.");
        }
    }

    /**
     * Recupera l'istanza. Se non esiste, la crea usando la factory.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SingletonI> T getOrCreate(Class<T> clazz, Supplier<T> factory) {
        return (T) registry.computeIfAbsent(clazz, k -> factory.get());
    }

    /**
     * Recupera l'istanza. Lancia eccezione se non presente.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SingletonI> T get(Class<T> clazz) {
        T instance = (T) registry.get(clazz);
        if (instance == null) {
            throw new IllegalStateException("Accesso negato: Il Singleton per [" +
                    clazz.getSimpleName() + "] non è stato ancora registrato/inizializzato.");
        }
        return instance;
    }
}