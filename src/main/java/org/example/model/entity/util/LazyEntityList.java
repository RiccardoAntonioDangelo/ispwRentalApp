package org.example.model.entity.util;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EntityDAO;
import org.example.model.dao.abstractfactory.EntityType;
import org.example.model.services.EntityI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce riferimenti ID.
 * T: Tipo dell'Entità (es. User)
 * I: Tipo dell'ID (es. String)
 */
public class LazyEntityList<T extends EntityI<I>, I extends Serializable> implements Serializable {

    private final List<I> ids = new ArrayList<>();
    private final EntityType entityType;

    // Prendiamo l'EntityType dal costruttore per sapere quale DAO usare
    public LazyEntityList(EntityType entityType) {
        this.entityType = entityType;
    }

    /**
     * Il tipo T è vincolato alla classe. Riceve l'entità, la salva e tiene l'ID.
     */
    @SuppressWarnings("unchecked")
    public void addAndSave(T entity) {
        if (entity == null) return;

        // Recuperiamo il DAO specifico per il tipo T tramite l'Enum
        EntityDAO<T> dao = (EntityDAO<T>) DAOManager.getEntity(entityType);

        if (dao.save(entity)) {
            I id = entity.getId();
            if (!ids.contains(id)) {
                ids.add(id);
            }
        } else {
            throw new RuntimeException("Errore salvataggio persistente per: " + entityType);
        }
    }

    /**
     * Ritorna l'oggetto di tipo T caricandolo on-demand
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {
        I id = ids.get(index);
        EntityDAO<T> dao = (EntityDAO<T>) DAOManager.getEntity(entityType);
        return dao.getById(String.valueOf(id));
    }

    public void addId(I id) {
        if (id != null && !ids.contains(id)) {
            ids.add(id);
        }
    }

    public int size() {
        return ids.size();
    }
}