package org.example.model.dao.filejson;

import org.example.model.entity.actors.User;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.actors.simple.Owner;
import org.example.model.entity.actors.strategy.UserStrategy;
import org.example.model.services.EntityI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class JsonFacade {

    // Definiamo la cartella radice dove verranno salvati tutti i dati del database
    private static final Path DATABASE_ROOT = Paths.get("database_data");
    private static final SmartJsonSerializer serializer = new SmartJsonSerializer(DATABASE_ROOT);

    // Blocco statico opzionale per registrare gli alias se ne hai bisogno globalmente
    static {
         serializer.registerAlias(Client.class, User.class.getSimpleName());
        serializer.registerAlias(Owner.class, User.class.getSimpleName());
        serializer.registerAlias(UserStrategy.class, User.class.getSimpleName());

    }

    // Costruttore privato: essendo una Facade/Utility non va istanziata
    private JsonFacade() {}

    /**
     * Scrive l'entità sul file JSON usando lo serializer intelligente
     */
    public static void writeEntity(String id, Object entity) throws IOException {
        if (entity instanceof EntityI<?>) {
            serializer.set((EntityI<?>) entity);
        } else {
            throw new IllegalArgumentException("L'entità deve implementare EntityI per usare SmartJsonSerializer");
        }
    }

    /**
     * Legge il file JSON sfruttando il meccanismo dinamico getSmart o get classico.
     * Visto che il DAO ti passa la classe attesa (entityClass), usiamo il get classico basato su classe.
     */
    public static <T> T read(Class<T> entityClass, String id) throws IOException {
        return serializer.get(id, entityClass);
    }

    /**
     * Rimuove il file JSON dal disco basandosi sulla classe e sull'ID
     */
    public static boolean remove(Class<?> entityClass, String id) {
        try {
            // Ricaviamo il nome della cartella (considerando eventuali alias)
            // Per farlo in modo pulito senza esporre troppi metodi, calcoliamo la cartella come fa lo serializer
            String folderName = entityClass.getSimpleName().toLowerCase(); 
            // Nota: Se usi gli alias, potresti voler aggiungere un metodo pubblico su SmartJsonSerializer tipo 'getFolderName(Class<?> clazz)'
            
            Path filePath = DATABASE_ROOT.resolve(folderName).resolve(id + ".json");
            File file = filePath.toFile();
            
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            System.err.println("Impossibile eliminare il file per ID: " + id);
            return false;
        }
    }

    /**
     * Legge tutti i file JSON presenti nella cartella della specifica entità
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAll(Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        String folderName = entityClass.getSimpleName().toLowerCase();
        File folder = DATABASE_ROOT.resolve(folderName).toFile();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        // Rimuoviamo l'estensione ".json" per ottenere l'ID
                        String id = file.getName().replace(".json", "");
                        T entity = serializer.get(id, entityClass);
                        if (entity != null) {
                            list.add(entity);
                        }
                    } catch (IOException e) {
                        System.err.println("Errore durante il caricamento massivo dal file: " + file.getName()+"eeee+"+e);
                    }
                }
            }
        }
        return list;
    }
}