package org.example.model.entity.session;

import org.example.model.entity.LazyEntityList;
import org.example.model.entity.actors.User;
import org.example.model.services.CollectionI;
import org.example.model.services.EntityI;
import org.example.model.services.WorkI;
import org.example.model.services.session.SessionI;
import org.example.model.services.user.UserI;

import java.util.HashMap;
import java.util.Map;

// NOTA: Se RentalOld estende una classe base (es. ObservableEntity),
// assicurati di estenderla anche qui per sbloccare il metodo notifyObservers.
public class Session implements SessionI {

    private User user;
    // Il campo rimane final e marcato transient per prevenire i problemi di serializzazione
    private final Map<String, CollectionI<?>> lazyCollections = new HashMap<>();

    public Session() {}

    public Session(User user) {
        this.user = user;
        this.commitChange();
    }

    /**
     * Centralizza la notifica delle modifiche di stato della sessione.
     */
    public void commitChange() {
        // Richiama il meccanismo di notifica ereditato o l'infrastruttura observer del tuo pattern
        // Se non hai ereditarietà diretta, adatta questa riga al bus di eventi del tuo progetto.
        this.notifyObservers(this);
    }


    public boolean isValid() { return this.user != null; }

    public boolean isValid(String password) {
        return isValid() && this.user.isValid(password);
    }


    public boolean execute(WorkI work) {
        return user.execute(this, work);
    }

    // --- Getter e Setter ---
    public String getUserid() {
        if(user!=null) return user.getId();
        return null;
    }

    public User getUser() { return user; }

    @Override
    public UserI getRole() {
        if(getUser()!=null)
         return getUser().getRole();
        return null;
    }

    public void setUser(User user) {
        this.user = user;
        this.commitChange(); // Cambia l'utente: notifica la modifica
    }

    @Override
    public boolean hasCollection(String key) { return lazyCollections.containsKey(key); }

    @Override
    public CollectionI getCollection(String key) { return lazyCollections.get(key); }

    @Override
    public void addCollection(String key) {
        this.addCollection(key, new LazyEntityList<>());
    }

    public void addCollection(String key, CollectionI<?> list) {
        this.lazyCollections.put(key, list);
        this.commitChange(); // Struttura dati modificata
    }

    @Override
    public void addItem(String key, EntityI item) {
        getCollection(key).add(item);
        this.commitChange(); // Elemento inserito in una collezione lazy
    }

    @Override
    public void removeItem(String key, EntityI item) {
        getCollection(key).remove(item);
        this.commitChange(); // Elemento rimosso da una collezione lazy
    }

    public Map<String, CollectionI<?>> getLazyCollections() { return lazyCollections; }

    public void setLazyCollections(Map<String, CollectionI<?>> lazyCollections) {
        this.lazyCollections.clear();
        if (lazyCollections != null) {
            this.lazyCollections.putAll(lazyCollections);
        }
        this.commitChange(); // Mappa interamente aggiornata
    }

    @Override
    public String getId() {
        return (user != null) ? user.getId() : null;
    }
}