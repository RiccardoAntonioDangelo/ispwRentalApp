package org.example.util.observer;

// Chi sta a guardare
public interface ObserverI<T> {
    void update(T eventData);
}

