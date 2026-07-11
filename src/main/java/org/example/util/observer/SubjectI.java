package org.example.util.observer;

import java.util.Collection;

public interface SubjectI<T> {

    /**
     * Ritorna una Collection (Set) degli osservatori.
     */
    default Collection<ObserverI<T>> getObserversList() {
        return InstanceObserverManager.getObservers(this);
    }

    default void attach(ObserverI<T> observer) {
        InstanceObserverManager.subscribe(this, observer);
    }

    default void detach(ObserverI<T> observer) {
        InstanceObserverManager.unsubscribe(this, observer);
    }

    default void notifyObservers(T eventData) {
        InstanceObserverManager.notify(this, eventData);
    }
}