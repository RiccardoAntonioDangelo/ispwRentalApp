package org.example.model.services;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * interfaccia di collezione Lazy totalmente basata sul tipo dell'elemento.
 * Eredita tutti i metodi di scansione nativi di Java (Stream, Iterator, for-each).
 *
 * @param <V> Il tipo dell'elemento contenuto (es. UserI, ProductI).
 */
public interface CollectionI<V> extends Collection<V> {

    /**
     * Recupera un elemento in modo Lazy sfruttando una scansione interna o il caricamento esterno.
     *
     * @param matcher      Un predicato (es. lambda x -> x.getId().equals("123")) per scansionare 
     * e cercare se l'elemento è già presente in memoria.
     * @param id           L'identificativo da passare al caricatore se l'elemento non è in memoria.
     * @param loadFunction La funzione per caricare l'oggetto dal database.
     * @return Un Optional con il valore trovato o caricato.
     */
    default Optional<V> get(Predicate<V> matcher, Object id, Function<Object, V> loadFunction) {
        // 1. Scansione degli elementi già in memoria (sfrutta lo Stream nativo della Collection)
        Optional<V> inMemory = this.stream().filter(matcher).findFirst();
        if (inMemory.isPresent()) {
            return inMemory;
        }

        // 2. Se la scansione non trova nulla, carichiamo on-demand
        try {
            V loadedElement = loadFunction.apply(id);
            if (loadedElement != null) {
                this.add(loadedElement); // Lo aggiungiamo alla collezione (cache)
            }
            return Optional.ofNullable(loadedElement);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}