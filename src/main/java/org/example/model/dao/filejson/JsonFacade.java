package org.example.model.dao.filejson;

import org.example.model.entity.actors.User;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.actors.simple.Owner;
import org.example.model.entity.actors.strategy.UserStrategy;
import org.example.model.services.EntityI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class JsonFacade {

    private static final Path DATABASE_ROOT = Paths.get("database_data");
    private static final SmartJsonSerializer serializer = new SmartJsonSerializer(DATABASE_ROOT);

    // 💡 Risolto: Costante per evitare la duplicazione delle stringhe letterali
    private static final String JSON_EXTENSION = ".json";

    static {
        serializer.registerAlias(Client.class, User.class.getSimpleName());
        serializer.registerAlias(Owner.class, User.class.getSimpleName());
        serializer.registerAlias(UserStrategy.class, User.class.getSimpleName());
    }

    private JsonFacade() {}

    public static void writeEntity(String id, Object entity) throws IOException {
        if (entity instanceof EntityI<?>) {
            serializer.set((EntityI<?>) entity);
        } else {
            throw new IllegalArgumentException("L'entità deve implementare EntityI per usare SmartJsonSerializer"+id);
        }
    }

    public static <T> T read(Class<T> entityClass, String id) throws IOException {
        return serializer.get(id, entityClass);
    }

    /**
     * Rimuove il file JSON dal disco usando java.nio.file.Files
     */
    public static boolean remove(Class<?> entityClass, String id) {
        try {
            String folderName = entityClass.getSimpleName().toLowerCase();
            // Costruiamo il percorso completo inserendo la costante dell'estensione
            Path filePath = DATABASE_ROOT.resolve(folderName).resolve(id + JSON_EXTENSION);

            // 💡 Risolto: Uso preferenziale di Files.delete() al posto di File.delete()
            Files.delete(filePath);
            return true;
        } catch (IOException e) {
            // Ritorna false se il file non esiste o non può essere cancellato
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
            // Utilizzo della costante anche nel filtro di scansione dei file
            File[] files = folder.listFiles((dir, name) -> name.endsWith(JSON_EXTENSION));
            if (files != null) {
                for (File file : files) {
                    try {
                        // Sostituzione sicura sfruttando la costante centralizzata
                        String id = file.getName().replace(JSON_EXTENSION, "");
                        T entity = serializer.get(id, entityClass);
                        if (entity != null) {
                            list.add(entity);
                        }
                    } catch (IOException e) {
                        // Eccezione ignorata come da richiesta precedente
                    }
                }
            }
        }
        return list;
    }
}