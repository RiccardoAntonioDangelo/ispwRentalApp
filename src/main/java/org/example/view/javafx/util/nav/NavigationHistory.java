package org.example.view.javafx.util.nav;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.LinkedList;

/**
 * Gestore generico della cronologia basato su una struttura a pila (Stack).
 * @param <T> Il tipo di dato da memorizzare (es. GraphicController)
 */
public class NavigationHistory<T> {

    // Usiamo LinkedList che implementa Deque, ottima per le operazioni in testa
    private final ObservableList<T> stack = FXCollections.observableList(new LinkedList<>());

    /**
     * Inserisce un elemento in cima alla cronologia.
     * Se l'elemento esiste già, rimuove tutti gli elementi sovrastanti
     * per tornare a quello stato (evita cicli infiniti nella navigazione).
     */
    public T push(T item) {
        if (item == null) return null;

        if (stack.contains(item)) {
            // Rimuove tutto ciò che sta SOPRA l'elemento trovato
            // Finché il primo elemento della lista non è quello cercato, lo elimina
            while (!stack.isEmpty() && !stack.getFirst().equals(item)) {
                stack.removeFirst();
            }
        } else {
            // Se è una nuova pagina, la aggiunge in cima
            stack.addFirst(item);
        }
        return item;
    }
    /**
     * Restituisce il primissimo elemento inserito nella cronologia (la base della pila),
     * senza rimuoverlo. Se la cronologia è vuota, restituisce null.
     */
    public T getFirstInserted() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.getLast();
    }

    /**
     * Rimuove l'elemento corrente in cima e lo restituisce.
     * Se la pila è vuota, restituisce null.
     */
    public T pop() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.removeFirst();
    }
    /**
     * Cerca l'indice di un elemento all'interno della cronologia.
     * Restituisce l'indice dell'elemento (0 se è in cima), oppure -1 se non esiste.
     */
    public int find(T item) {
        if (item == null) return -1;
        return stack.indexOf(item);
    }

    /**
     * Rimuove tutti gli elementi a partire dalla cima (indice 0) fino a raggiungere
     * l'indice specificato (escluso). L'elemento all'indice target diventerà la nuova cima.
     */
    public void removeUntilIndex(int targetIndex) {
        if (targetIndex < 0 || targetIndex >= stack.size()) {
            throw new IndexOutOfBoundsException("Indice target non valido nella cronologia: " + targetIndex);
        }

        // Finché l'indice target non diventa la nuova cima (indice 0), rimuoviamo la testa
        while (targetIndex > 0) {
            stack.removeFirst();
            targetIndex--; // Scaliamo l'indice perché la lista si sta accorciando
        }
    }

    /**
     * Restituisce l'elemento attualmente in cima (schermata corrente) senza rimuoverlo.
     */
    public T peek() {
        return stack.isEmpty() ? null : stack.getFirst();
    }

    /**
     * Restituisce il numero di elementi attualmente registrati nella cronologia.
     */
    public int size() {
        return stack.size();
    }

    public void addListener(ListChangeListener<T> listener) {
        stack.addListener(listener);
    }

    public ObservableList<T> getAll() {
        return stack;
    }

    public void clear() {
        stack.clear();
    }

    @Override
    public String toString() {
        return "NavigationStack: " + stack.toString();
    }
}