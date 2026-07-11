package org.example.model.dao.file.TEST;

import org.example.model.dao.file.dao.FileSessionDAO;
import org.example.model.entity.LazyEntityList;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.product.Product;
import org.example.model.entity.session.Session;

public class FileSessionDAOTest {

    public static void main(String[] args) {
        try {
            System.out.println("=== START FILE SESSION DAO TEST ===");

            // 1. Inizializzazione DAO
            FileSessionDAO sessionDao = new FileSessionDAO();

            // 2. Creazione dell'oggetto di Dominio (Session)
            Client michele = new Client("michele@TEST.it", "password123");
            michele.setName("Michele");
            
            Session sessionOriginal = new Session(michele);
            
            // Aggiungiamo una collezione lazy per testare la profondità
            LazyEntityList<Product> cart = new LazyEntityList<>();
            cart.add(new Product("prod_001", "Smartphone", 7993.0));
            sessionOriginal.addCollection("cart", cart);

            // 3. Test SAVE (Dominio -> DTO -> File)
            System.out.println("-> Salvataggio sessione...");
            boolean saved = sessionDao.save(sessionOriginal);
            assertTrue(saved, "Il salvataggio della sessione è fallito");

            // 4. Test GET BY ID (File -> DTO -> Dominio)
            System.out.println("-> Recupero sessione tramite ID...");
            Session loadedSession = sessionDao.getById(sessionOriginal.getId());

            // 5. Validazione dei dati caricati
            assertNotNull(loadedSession, "La sessione caricata è null");
            assertEquals(sessionOriginal.getId(), loadedSession.getId(), "L'ID della sessione non corrisponde");
            
            // Verifica che l'utente sia stato ricostruito correttamente
            assertNotNull(loadedSession.getUser(), "L'utente nella sessione caricata è null");
            assertEquals("Michele", loadedSession.getUser().getName(), "Il nome dell'utente non corrisponde");

            // Verifica della collezione lazy
            LazyEntityList<?> loadedCart = (LazyEntityList<?>) loadedSession.getLazyCollections().get("cart");
            assertNotNull(loadedCart, "La collezione 'cart' non è stata caricata");
            assertEquals(1, loadedCart.getFullList().size(), "La dimensione della collezione non corrisponde");

            System.out.println("\n=== TEST COMPLETATO CON SUCCESSO! ===");
            System.out.println("Verifica manuale: Controlla se in 'data/sessions/" + michele.getId() + ".json' " +
                               "i riferimenti sono corretti.");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST FALLITO !!!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    // --- Metodi Helper per asserzioni veloci ---
    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new RuntimeException(message);
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) throw new RuntimeException(message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new RuntimeException(message + " [Atteso: " + expected + ", Ricevuto: " + actual + "]");
        }
    }
}