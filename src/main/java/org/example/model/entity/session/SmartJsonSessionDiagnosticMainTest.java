package org.example.model.entity.session;

import org.example.model.dao.filejson.SmartJsonSerializer;
import org.example.model.entity.LazyEntityList;
import org.example.model.entity.actors.User;
import org.example.model.services.CollectionI;

import java.nio.file.Path;

/**
 * Test mirato: verifica se il ClassCastException nasce dentro
 * SmartJsonSerializer quando salva/ricarica una Session con
 * una LazyEntityList popolata (es. il campo lazyCollections).
 */
public class SmartJsonSessionDiagnosticMainTest {

    public static void main(String[] args) throws Exception {
        Path tempDir = Path.of("diagnostic-data");
        SmartJsonSerializer serializer = new SmartJsonSerializer(tempDir);

        // 1. Costruisci una Session "vera" come farebbe l'app
        User user = new User("test@test.com", "pwd");
        Session original = new Session(user);
        original.ensureCollection("RENTAL_TEST_KEY"); // crea una vera LazyEntityList

        System.out.println("Tipo PRIMA del salvataggio: "
                + original.getLazyCollections().get("RENTAL_TEST_KEY").getClass());

        // 2. Salva su file tramite SmartJsonSerializer (esattamente come fa l'app)
        serializer.set(original);
        System.out.println("Salvataggio completato.");

        // 3. Ricarica la Session da file
        Session restored = serializer.get(original.getId(), Session.class);

        if (restored == null) {
            System.out.println("FALLITO: la Session ricaricata è null");
            return;
        }

        Object rawCollection = restored.getLazyCollections().get("RENTAL_TEST_KEY");
        System.out.println("Tipo DOPO il caricamento: "
                + (rawCollection == null ? "null" : rawCollection.getClass()));

        // 4. Il test vero: prova a usarla come farebbe addItem()
        try {
            CollectionI<?> c = restored.getCollection("RENTAL_TEST_KEY");
            System.out.println(c instanceof LazyEntityList
                    ? "OK: tipo corretto dopo il round-trip"
                    : "FALLITO: tipo sbagliato ma nessun cast ancora avvenuto");
        } catch (ClassCastException e) {
            System.out.println("RIPRODOTTO IL BUG: ClassCastException identico a produzione!");
            e.printStackTrace();
        }
    }
}