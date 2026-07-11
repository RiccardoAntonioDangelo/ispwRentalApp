package org.example.model.entity;

import org.example.model.services.CollectionI;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class LazyEntityList<T extends Serializable> implements Serializable, CollectionI<T> {
    public LazyEntityList() {
        super();
    }
    // La nostra memoria/cache interna
    private final List<T> list = new ArrayList<>();

    // --- IMPLEMENTAZIONE DEL CUORE LAZY (DALLA TUA INTERFACCIA) ---

    @Override
    public Optional<T> get(Predicate<T> matcher, Object id, Function<Object, T> loadFunction) {
        // 1. Scansione rapida della memoria interna tramite Stream
        Optional<T> inMemory = this.list.stream().filter(matcher).findFirst();
        if (inMemory.isPresent()) {
            return inMemory;
        }

        // 2. Se non è in memoria, attiviamo il caricamento On-Demand
        try {
            T loadedElement = loadFunction.apply(id);
            if (loadedElement != null) {
                this.add(loadedElement); // Salviamo in memoria per i prossimi accessi
            }
            return Optional.ofNullable(loadedElement);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // --- DELEGA DEI METODI STANDARD DI COLLECTION A 'list' ---

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T element) {
        return list.add(element);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    // --- METODI EXTRA DI UTILITÀ ---

    public List<T> getFullList() {
        return list;
    }
}