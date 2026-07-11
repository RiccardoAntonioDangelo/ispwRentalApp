package org.example.model.dao.file.dao;

import org.example.model.dao.file.utility.JsonFacade;

import java.util.List;
import java.util.function.Function;

public abstract class FileEntityDAO<T> {
    protected final Class<T> entityClass;
    private final Function<T, String> idExtractor;
    protected FileEntityDAO(Class<T> entityClass) {
        this(entityClass,null);
    }
    protected FileEntityDAO(Class<T> entityClass, Function<T, String> idExtractor) {
        this.entityClass = entityClass;
        this.idExtractor = idExtractor;
    }

    /**
     * Estrae l'ID dall'entità. Se è stata passata una funzione nel costruttore usa quella,
     * altrimenti si aspetta un override nelle sottoclassi.
     */
    protected String getEntityId(T entity) {
        if (idExtractor != null) {
            return idExtractor.apply(entity);
        }
        throw new UnsupportedOperationException("Devi implementare getEntityId o passare una Function nel costruttore");
    }

    public T getById(String id) {
        if (id == null || id.isBlank()) return null;
        try {
            return JsonFacade.read(entityClass, id);
        } catch (Exception e) {
            System.err.println("Errore lettura " + entityClass.getSimpleName() + " ID: " + id);
            return null;
        }
    }

    public boolean save(T entity) {
        if (entity == null) return false;
        try {
            String id = getEntityId(entity);

            // Protezione critica: se l'ID è nullo, FileBaseDAO scriverebbe un filedeprecated invalido
            if (id == null || id.isBlank()) {
                System.err.println("Impossibile salvare: ID nullo per " + entityClass.getSimpleName());
                return false;
            }

            JsonFacade.writeEntity( id, entity);
            return true;
        } catch (Exception e) {
            // Stampiamo l'errore per capire se Jackson sta fallendo (es. No content to map)
            System.err.println("ERRORE SALVATAGGIO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String id) {
        if (id == null) return false;
        return JsonFacade.remove(entityClass, id);
    }

    public List<T> getAll() {
        return JsonFacade.getAll(entityClass);
    }



}