package org.example.model.dao.filesimple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.function.Function;

public class JacksonUtil {

    // Interfaccia funzionale per gestire la scrittura che lancia eccezioni checked
    @FunctionalInterface
    public interface SganciaStringa<T> {
        String applica(T oggetto) throws Exception;
    }

    /**
     * Generalizza la registrazione inline di Serializer/Deserializer basati su Stringhe
     */
    public static <T> void registraTipoCustom(
            SimpleModule modulo, 
            Class<T> classeTarget, 
            SganciaStringa<T> scrittore, 
            Function<String, T> lettore) {

        // Serializer generico al volo
        modulo.addSerializer(classeTarget, new JsonSerializer<T>() {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                try {
                    gen.writeString(scrittore.applica(value));
                } catch (Exception e) {
                    throw new IOException("Errore custom di serializzazione per " + classeTarget.getSimpleName(), e);
                }
            }
        });

        // Deserializer generico al volo
        modulo.addDeserializer(classeTarget, new JsonDeserializer<T>() {
            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String testo = p.getValueAsString();
                return lettore.apply(testo);
            }
        });
    }
}