package org.example.model.dao.file.utility;

import org.example.exceptions.dao.file.EntityNotFoundException;
import org.example.exceptions.dao.file.PersistenceException;
import org.example.exceptions.logsystem.LogSystem;
import org.example.model.entity.actors.User;
import org.example.model.entity.product.Product;
import org.example.model.entity.rental.RentalOld;
import org.example.model.entity.session.Session;
import org.example.model.services.EntityI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Facade per la persistenza JSON. 
 * Coordina il TypeRegistry (gestione percorsi e alias) 
 * e il JsonGraphManager (serializzazione e risoluzione dei grafi).
 */
public final class JsonFacade {
    private JsonFacade() {}


    private static final ManualTypeRegistry typeRegistry=new ManualTypeRegistry(Path.of("file_storage"));
    private static final JsonGraphManager graphManager= new JsonGraphManager(typeRegistry);
    static {
        typeRegistry.addAlias(User.class);
        typeRegistry.register(Product.class,typeRegistry.aliasOf(Product.class));
        typeRegistry.register(RentalOld.class,typeRegistry.aliasOf(RentalOld.class));
        typeRegistry.register(Session.class,typeRegistry.aliasOf(Session.class));
    }
    /**
     * Registra un'entità con alias automatico (nome classe minuscolo).
     */
    public static void registerEntity(Class<? extends EntityI<?>> clazz) {
        typeRegistry.register(clazz,typeRegistry.aliasOf(clazz));
    }

    /**
     * Registra un'entità con supporto alla risalita gerarchica (ManualTypeRegistry).
     */
    public static void registerEntityWithHierarchy(Class<? extends EntityI<?>> clazz) {
        typeRegistry.addAlias(clazz);
    }
    /**
     * Salva un'entità. Se contiene riferimenti ad altre EntityI registrate, 
     * verranno salvate come riferimenti {_ref, _folder}.
     */
    public static void writeEntity(String id,Object entity) {
        try {
            graphManager.save(entity,typeRegistry.entityPathStr(entity.getClass(),id));
        } catch (IOException e) {
            throw new PersistenceException("Errore durante il salvataggio dell'entità [ID: " +id + "]", e);
        }
    }

    /**
     * Carica un'entità e risolve ricorsivamente tutti i riferimenti nel grafo JSON.
     */
    public static <T> T read(Class<T> expectedType, Object id) throws EntityNotFoundException {
        if (expectedType == null || id == null) return null;

        String path = typeRegistry.entityPathStr(expectedType, id.toString());
        try {
            T result = graphManager.load(path, expectedType);
            if (result == null) {throw new EntityNotFoundException("Risorsa non trovata: " + path);}
            return result;
        } catch (IOException e) {
            throw new PersistenceException("Errore durante il caricamento di: " + path, e);
        }
    }

    /**
     * Elimina il file fisico dell'entità.
     */
    public static boolean remove(Class<?> clazz, Object id) {
        if (clazz == null || id == null) return false;
        try {
            return graphManager.delete(typeRegistry.entityPathStr(clazz,id.toString()));
        } catch (IOException e) {
            throw new PersistenceException("Errore durante l'eliminazione del file [ID: " + id + "]", e);
        }
    }

    /**
     * Recupera tutte le entità di un determinato tipo presenti nel file system.
     * Itera sui file della cartella dedicata e carica ogni entità risolvendo i riferimenti.
     */
    public static <T> List<T> getAll(Class<T> expectedType) {
        // Controllo preventivo per evitare Null Pointer
        if (expectedType == null) {return Collections.emptyList();}
        String folderName = typeRegistry.folderOf(expectedType);
        Path folderPath = typeRegistry.resolvePath(folderName);
        File folder = folderPath.toFile();

        if (!folder.exists() || !folder.isDirectory()) {return Collections.emptyList();}

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (files == null) {return Collections.emptyList();}

        List<T> results = new ArrayList<>();
        for (File file : files) {
            T entity = safeRead(expectedType, file);
            if (entity != null) {
                results.add(entity);
            }
        }
        return results;
    }
    private static <T> T safeRead(Class<T> expectedType, File file) {
        String fileName = file.getName();
        String id = fileName.substring(0, fileName.lastIndexOf('.'));

        try {
            return read(expectedType, id);
        } catch (EntityNotFoundException e) {
            LogSystem.errorType("Entità non trovata durante la scansione: {0}"+ fileName);
        } catch (PersistenceException e) {
            LogSystem.errorType("Errore di persistenza sul file: " + fileName+ e);
        }
        return null;
    }



    // --- Getters per componenti interni ---
    public static ManualTypeRegistry getTypeRegistry() { return typeRegistry; }
    public static JsonGraphManager getGraphManager() { return graphManager; }
}