package org.example.model.dao.filesimple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class JsonEntityManager {

    private final ObjectMapper mapper;
    private final SimpleModule customModule;

    // Interfaccia funzionale per gestire la scrittura di tipi complessi che possono lanciare eccezioni
    @FunctionalInterface
    public interface CustomWriter<T> {
        String apply(T obj) throws Exception;
    }

    public JsonEntityManager() {
        this.mapper = new ObjectMapper();
        this.customModule = new SimpleModule();
        // Registriamo il modulo vuoto iniziale, lo popoleremo dinamicamente
        this.mapper.registerModule(customModule);
    }

    /**
     * METODO RICHIESTO: Prende un'entità e la salva PER INTERO su un file specifico.
     * Gestisce automaticamente la creazione delle cartelle se non esistono.
     */
    public <T> void salvaEntitaCompleta(File targetFile, T entity) throws IOException {
        if (entity == null) {
            throw new IllegalArgumentException("L'entità da salvare non può essere nulla");
        }

        // Se le cartelle di destinazione non esistono, le crea automaticamente
        if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        // Scrive l'intera classe nel file strutturandolo in modo leggibile (Pretty Printer)
        mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, entity);
    }

    /**
     * METODO DI LETTURA COMPLEMENTARE: Legge l'intero file JSON e ricostruisce la classe.
     */
    public <T> T caricaEntitaCompleta(File sourceFile, Class<T> targetClass) throws IOException {
        if (!sourceFile.exists()) {
            return null;
        }
        return mapper.readValue(sourceFile, targetClass);
    }

    /**
     * SPIEGA A JACKSON COME FARE: Generalizza le regole per le classi complesse.
     * Permette di impostare metodi custom inline senza creare file aggiuntivi.
     */
    public <C> JsonEntityManager configuraTipoComplesso(
            Class<C> classeTarget, 
            CustomWriter<C> scrittoreCustom, 
            Function<String, C> lettoreCustom) {

        // Serializer custom inline
        customModule.addSerializer(classeTarget, new JsonSerializer<C>() {
            @Override
            public void serialize(C value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                try {
                    gen.writeString(scrittoreCustom.apply(value));
                } catch (Exception e) {
                    throw new IOException("Errore nella serializzazione custom di: " + classeTarget.getSimpleName(), e);
                }
            }
        });

        // Deserializer custom inline
        customModule.addDeserializer(classeTarget, new JsonDeserializer<C>() {
            @Override
            public C deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String stringaJson = p.getValueAsString();
                return lettoreCustom.apply(stringaJson);
            }
        });

        return this; // Permette la configurazione fluida (chaining)
    }
}