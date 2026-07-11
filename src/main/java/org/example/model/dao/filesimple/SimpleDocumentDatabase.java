package org.example.model.dao.filesimple;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.model.services.EntityI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SimpleDocumentDatabase {

    private static final String TYPE_KEY = "_type";
    private final Path basePath;
    private final ObjectMapper mapper;

    /**
     * Il costruttore imposta la cartella radice principale (es. "database")
     */
    public SimpleDocumentDatabase(Path rootPath) {
        this.basePath = rootPath.resolve("data");
        this.mapper = new ObjectMapper();
        this.mapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Disattiviamo del tutto i getter/setter tradizionali per evitare che leggano metodi indesiderati
        this.mapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        this.mapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        this.mapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);

        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.mapper.setVisibility(
                com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std.defaultInstance()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // AGGIUNGI QUESTA RIGA QUI:
        this.mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
    /**
     * SALVA: Prende QUALSIASI entità, scopre la cartella dal nome della classe,
     * inietta il tipo nel JSON e scrive tutto nel file.
     */
    public void save(EntityI<?> entity) throws IOException {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("L'entità o il suo ID non possono essere null");
        }

        // 1. Ricava dinamicamente il nome della classe in minuscolo (es: "user")
        String folderName = entity.getClass().getSimpleName().toLowerCase();
        String fileName = entity.getId().toString() + ".json";
        
        // 2. Costruisci il percorso finale: data / nome_classe / id.json
        Path finalPath = basePath.resolve(folderName).resolve(fileName);
        File file = finalPath.toFile();
        
        // Crea le cartelle se non esistono
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        // 3. Converti l'oggetto in albero JSON e inietta il tipo completo per il polimorfismo
        JsonNode tree = mapper.valueToTree(entity);
        if (tree.isObject()) {
            ((ObjectNode) tree).put(TYPE_KEY, entity.getClass().getName());
        }

        // 4. Scrivi il file su disco
        mapper.writeValue(file, tree);
    }

    /**
     * RECUPERA: Cerca il file nella cartella della classe specificata e lo ricostruisce.
     */
    public <T extends EntityI<?>> T load(String id, Class<T> targetClass) throws IOException {
        if (id == null) return null;

        // Trova la cartella basandosi sulla classe che ti aspetti di ricevere
        String folderName = targetClass.getSimpleName().toLowerCase();
        Path finalPath = basePath.resolve(folderName).resolve(id + ".json");
        File file = finalPath.toFile();
        
        if (!file.exists()) return null;

        // 1. Legge il JSON grezzo
        JsonNode rawJson = mapper.readTree(file);

        // 2. Controlla se c'è un sotto-tipo specifico salvato nel file (es. Client al posto di User)
        Class<?> actualClass = targetClass;
        if (rawJson.has(TYPE_KEY)) {
            String className = rawJson.get(TYPE_KEY).asText();
            try {
                actualClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IOException("Classe non trovata nel sistema: " + className, e);
            }
        }

        // 3. Converte il JSON nell'oggetto Java finale castandolo alla classe corretta
        return targetClass.cast(mapper.treeToValue(rawJson, actualClass));
    }

    /**
     * ELIMINA: Rimuove il file corrispondente dal database.
     */
    public boolean delete(EntityI<?> entity) throws IOException {
        if (entity == null || entity.getId() == null) return false;
        String folderName = entity.getClass().getSimpleName().toLowerCase();
        Path finalPath = basePath.resolve(folderName).resolve(entity.getId().toString() + ".json");
        return Files.deleteIfExists(finalPath);
    }
}