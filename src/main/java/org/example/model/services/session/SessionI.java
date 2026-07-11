package org.example.model.services.session;


import org.example.model.services.CollectionI;
import org.example.model.services.EntityI;
import org.example.model.services.user.UserI;

public interface SessionI extends EntityI<String> {
    /**
     * Verifica se una collezione esiste.
     */
    boolean hasCollection(String key);
    CollectionI getCollection(String key);

    /**
     * Aggiunge una nuova collezione (sovrascrive se già presente).
     */
    void addCollection(String key);

    /**
     *  Aggiunge la collezione solo se non esiste già.
     * È un metodo default perché usa gli altri due metodi dell'interfaccia.
     */
    default void ensureCollection(String key) {if(!hasCollection(key)) {addCollection(key);}}


    void addItem( String key,EntityI<String> item);
    void removeItem(String key,EntityI<String> item);
    UserI getUser(); // Sostituisci Object con il tuo tipo User reale (es. UserI o String id)
    UserI getRole(); // Sostituisci Object con il tuo tipo User reale (es. UserI o String id)

}