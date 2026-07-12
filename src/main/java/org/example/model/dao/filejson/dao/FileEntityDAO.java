package org.example.model.dao.filejson.dao;

import org.example.model.dao.filejson.JsonFacade;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe astratta base per la gestione del Data Access Object (DAO) su file JSON.
 * Fornisce le operazioni CRUD fondamentali (Create, Read, Update, Delete) per le entità.
 *
 * @param <T> Il tipo dell'entità gestita dal DAO.
 */
public abstract class FileEntityDAO<T> {

    // Logger per sostituire System.err (richiesto dagli standard SonarQube)
    private static final Logger logger = Logger.getLogger(FileEntityDAO.class.getName());

    protected final Class<T> entityClass;
    private final Function<T, String> idExtractor;

    /**
     * Costruttore base che richiede solo la classe dell'entità.
     * Se usato, la sottoclasse dovrà fare l'override di getEntityId.
     */
    protected FileEntityDAO(Class<T> entityClass) {
        this(entityClass, null);
    }

    /**
     * Costruttore completo che accetta una funzione per estrarre l'ID dall'entità.
     */
    protected FileEntityDAO(Class<T> entityClass, Function<T, String> idExtractor) {
        this.entityClass = entityClass;
        this.idExtractor = idExtractor;
    }

    /**
     * Estrae l'ID dall'entità passata come parametro.
     * Se è stata fornita una funzione nel costruttore usa quella,
     * altrimenti si aspetta un override nelle sottoclassi.
     */
    protected String getEntityId(T entity) {
        if (idExtractor != null) {
            return idExtractor.apply(entity);
        }
        throw new UnsupportedOperationException("Devi implementare getEntityId o passare una Function nel costruttore");
    }

    /**
     * Recupera un'entità tramite il suo ID.
     * * @param id L'identificativo univoco dell'entità.
     * @return L'entità trovata, oppure null in caso di errore o se l'ID non è valido.
     */
    public T getById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            return JsonFacade.read(entityClass, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Errore lettura " + entityClass.getSimpleName() + " ID: " + id);
            return null;
        }
    }

    /**
     * Salva o aggiorna un'entità su file JSON.
     * * @param entity L'entità da salvare.
     * @return true se il salvataggio è andato a buon fine, false altrimenti.
     */
    public boolean save(T entity) {
        if (entity == null) {
            return false;
        }
        try {
            String id = getEntityId(entity);

            // Protezione critica: evita la scrittura di file invalidi se l'ID manca
            if (id == null || id.isBlank()) {
                logger.log(Level.WARNING, () -> "Impossibile salvare: ID nullo per " + entityClass.getSimpleName());
                return false;
            }

            JsonFacade.writeEntity(id, entity);
            return true;
        } catch (Exception e) {
            // Sostituito e.printStackTrace() con il logging corretto della stack trace
            logger.log(Level.SEVERE, e, () -> "ERRORE SALVATAGGIO su " + entityClass.getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un'entità tramite il suo ID.
     * * @param id L'identificativo dell'entità da rimuovere.
     * @return true se l'eliminazione ha avuto successo, false altrimenti.
     */
    public boolean delete(String id) {
        if (id == null) {
            return false;
        }
        return JsonFacade.remove(entityClass, id);
    }

    /**
     * Recupera tutte le entità di questo tipo presenti nel sistema.
     * * @return Una lista contenente tutte le entità.
     */
    public List<T> getAll() {
        return JsonFacade.getAll(entityClass);
    }
}