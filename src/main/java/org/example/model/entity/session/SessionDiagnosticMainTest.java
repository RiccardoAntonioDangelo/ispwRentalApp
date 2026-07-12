package org.example.model.entity.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.example.model.entity.LazyEntityList;
import org.example.model.entity.actors.User;
import org.example.model.services.CollectionI;

import java.util.ArrayList;
import java.util.Map;

/**
 * Main di diagnostica: NON è il main dell'app, è solo per capire
 * dove nasce il ClassCastException su Session/CollectionI.
 * Esegui questo, guarda quale test stampa "FALLITO" prima degli altri.
 */
public class SessionDiagnosticMainTest {

    public static void main(String[] args) {
        test1_flussoRuntimePuro();
        test2_riproduzioneManuale();
        test3_jacksonSenzaTyping();
        test4_jacksonConTyping();
    }

    // ==========================================================
    // TEST 1: solo runtime, nessuna serializzazione.
    // Se OK, la logica di Session/ensureCollection è a posto.
    // ==========================================================
    static void test1_flussoRuntimePuro() {
        System.out.println("\n=== TEST 1: flusso runtime puro ===");
        try {
            Session session = new Session(new User("test@test.com", "pwd"));
            String key = "RENTAL_TEST_KEY";

            session.ensureCollection(key);
            CollectionI<?> c = session.getCollection(key);

            System.out.println("Tipo ottenuto: " + c.getClass());
            System.out.println(c instanceof LazyEntityList ? "OK" : "FALLITO: non è LazyEntityList");
        } catch (Exception e) {
            System.out.println("FALLITO con eccezione:");
            e.printStackTrace();
        }
    }

    // ==========================================================
    // TEST 2: riproduzione manuale del bug (ArrayList grezzo in mappa).
    // Serve solo a confermare il meccanismo dell'errore.
    // ==========================================================
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void test2_riproduzioneManuale() {
        System.out.println("\n=== TEST 2: ArrayList grezzo forzato nella mappa ===");
        try {
            Session session = new Session(new User("test@test.com", "pwd"));
            String key = "RENTAL_TEST_KEY";

            Map rawMap = session.getLazyCollections();
            rawMap.put(key, new ArrayList<>()); // simula quello che farebbe Jackson senza typing

            session.getCollection(key); // qui ci aspettiamo il crash
            System.out.println("FALLITO: doveva lanciare ClassCastException e non l'ha fatto");
        } catch (ClassCastException e) {
            System.out.println("OK: riprodotto il ClassCastException atteso -> " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FALLITO con eccezione inattesa:");
            e.printStackTrace();
        }
    }

    // ==========================================================
    // TEST 3: round-trip JSON con ObjectMapper "nudo" (senza polymorphic typing).
    // Questo è il test decisivo: se fallisce, la causa reale è qui.
    // ==========================================================
    static void test3_jacksonSenzaTyping() {
        System.out.println("\n=== TEST 3: round-trip Jackson SENZA polymorphic typing ===");
        try {
            ObjectMapper mapper = new ObjectMapper();

            Session original = new Session(new User("test@test.com", "pwd"));
            original.ensureCollection("RENTAL_TEST_KEY");

            String json = mapper.writeValueAsString(original);
            System.out.println("JSON generato:\n" + json);

            Session restored = mapper.readValue(json, Session.class);
            CollectionI<?> restoredCollection = restored.getLazyCollections().get("RENTAL_TEST_KEY");

            System.out.println("Tipo dopo deserializzazione: " +
                    (restoredCollection == null ? "null" : restoredCollection.getClass()));

            if (restoredCollection instanceof LazyEntityList) {
                System.out.println("OK (inatteso): ha comunque ricostruito LazyEntityList");
            } else {
                System.out.println("FALLITO (come sospettato): Jackson ha ricostruito un " +
                        (restoredCollection == null ? "null" : restoredCollection.getClass()) +
                        " invece di LazyEntityList -> QUESTA è la causa del bug");
            }
        } catch (Exception e) {
            System.out.println("FALLITO con eccezione durante il round-trip:");
            e.printStackTrace();
        }
    }

    // ==========================================================
    // TEST 4: stesso round-trip, ma CON polymorphic typing configurato
    // come già fai in FileEntityDAO. Deve funzionare: è la conferma della fix.
    // ==========================================================
    static void test4_jacksonConTyping() {
        System.out.println("\n=== TEST 4: round-trip Jackson CON polymorphic typing ===");
        try {
            ObjectMapper mapper = new ObjectMapper();
            BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType(CollectionI.class)
                    .allowIfSubType("org.example.model.entity")
                    .build();
            mapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);

            Session original = new Session(new User("test@test.com", "pwd"));
            original.ensureCollection("RENTAL_TEST_KEY");

            String json = mapper.writeValueAsString(original);
            System.out.println("JSON generato:\n" + json);

            Session restored = mapper.readValue(json, Session.class);
            CollectionI<?> restoredCollection = restored.getLazyCollections().get("RENTAL_TEST_KEY");

            System.out.println("Tipo dopo deserializzazione: " + restoredCollection.getClass());
            System.out.println(restoredCollection instanceof LazyEntityList
                    ? "OK: la fix funziona"
                    : "FALLITO: la fix non basta, serve altro");
        } catch (Exception e) {
            System.out.println("FALLITO con eccezione:");
            e.printStackTrace();
        }
    }
}