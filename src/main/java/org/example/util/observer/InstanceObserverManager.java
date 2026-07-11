package org.example.util.observer;

import java.util.*;

public class InstanceObserverManager {
    // Usiamo Set invece di List per evitare duplicati a livello di struttura dati
    private static final Map<Object, Set<ObserverI<?>>> INSTANCE_REGISTRY = new WeakHashMap<>();

    private InstanceObserverManager() {}

    @SuppressWarnings("unchecked")
    public static <T> Set<ObserverI<T>> getObservers(Object instance) {
        return (Set<ObserverI<T>>) (Set<?>) INSTANCE_REGISTRY.computeIfAbsent(instance, k -> new LinkedHashSet<>());
    }

    /**
     * Gestisce l'aggiunta sicura di un osservatore
     */
    public static <T> void subscribe(Object instance, ObserverI<T> observer) {
        if (instance != null && observer != null) {
            getObservers(instance).add((ObserverI<Object>) observer);
        }
    }

    /**
     * Gestisce la rimozione
     */
    public static <T> void unsubscribe(Object instance, ObserverI<T> observer) {
        Set<ObserverI<T>> observers = getObservers(instance);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    public static <T> void notify(Object instance, T eventData) {
        Set<ObserverI<T>> observers = getObservers(instance);
        if (observers != null && !observers.isEmpty()) {
            // Copia in lista per evitare ConcurrentModificationException
            for (ObserverI<T> obs : new ArrayList<>(observers)) {
                obs.update(eventData);
            }
        }
    }
}