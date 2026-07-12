package org.example.model.entity.util;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EntityDAO;
import org.example.model.dao.abstractfactory.EntityType;
import org.example.model.services.EntityI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gestisce una lista di riferimenti a entità caricati in modalità "Lazy" tramite il loro ID.
 * Riduce l'occupazione di memoria mantenendo solo gli ID e caricando gli oggetti on-demand.
 *
 * @param <T> Il tipo dell'Entità (deve implementare {@link EntityI}).
 * @param <I> Il tipo dell'identificativo (deve essere {@link Serializable}).
 */
public class LazyEntityList<T extends EntityI<I>, I extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L; // Consigliato da SonarQube per classi Serializable

    private final List<I> ids = new ArrayList<>();
    private final EntityType entityType;

    /**
     * Costruttore che definisce il tipo di entità gestita per mappare correttamente il DAO.
     *
     * @param entityType L'enum che rappresenta il tipo di entità.
     */
    public LazyEntityList(EntityType entityType) {
        this.entityType = entityType;
    }

    /**
     * Riceve un'entità, tenta il salvataggio persistente tramite DAO e ne memorizza l'ID.
     *
     * @param entity L'entità da salvare e tracciare.
     * @throws IllegalStateException Se il salvataggio sul database/file fallisce.
     */
    public void addAndSave(T entity) {
        if (entity == null) {
            return;
        }

        // Il cast viene isolato e documentato per SonarQube
        @SuppressWarnings("unchecked")
        EntityDAO<T> dao = (EntityDAO<T>) DAOManager.getEntity(entityType);

        if (dao.save(entity)) {
            I id = entity.getId();
            if (id != null && !ids.contains(id)) {
                ids.add(id);
            }
        } else {
            // SonarQube preferisce eccezioni mirate (IllegalStateException) rispetto a RuntimeException generiche
            throw new IllegalStateException("Errore salvataggio persistente per il tipo: " + entityType);
        }
    }

    /**
     * Recupera l'oggetto di tipo T caricandolo on-demand (Lazy Loading) tramite il suo indice.
     *
     * @param index L'indice dell'ID nella lista interna.
     * @return L'entità completa recuperata dal DAO, oppure null se non trovata.
     * @throws IndexOutOfBoundsException Se l'indice è fuori dai limiti della lista.
     */
    public T get(int index) {
        I id = ids.get(index);

        @SuppressWarnings("unchecked")
        EntityDAO<T> dao = (EntityDAO<T>) DAOManager.getEntity(entityType);

        // Uso di Objects.toString per gestire in sicurezza eventuali ID particolari ed evitare NPE nascosti
        return dao.getById(Objects.toString(id, null));
    }

    /**
     * Aggiunge direttamente un ID alla lista di riferimenti, se non è già presente.
     *
     * @param id L'identificativo da aggiungere.
     */
    public void addId(I id) {
        if (id != null && !ids.contains(id)) {
            ids.add(id);
        }
    }

    /**
     * Restituisce il numero di elementi (ID) attualmente tracciati.
     *
     * @return Il numero di riferimenti nella lista.
     */
    public int size() {
        return ids.size();
    }
}