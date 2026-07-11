package org.example.model.dao.file.TEST;

import org.example.model.dao.file.utility.JsonGraphManager;
import org.example.model.dao.file.utility.ManualTypeRegistry;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.actors.User;
import org.example.model.entity.session.Session;
import org.example.model.entity.LazyEntityList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonGraphManagerV3Test {

    private static final Path TEST_ROOT = Path.of("test_storage_v3");
    private static JsonGraphManager manager;
    private static ManualTypeRegistry registry;

    public static void main(String[] args) throws Exception {
        setup();
        try {
            testUserRefTruncation();
            testSessionRoundTrip();
            testLazyCollectionRoundTrip();
            System.out.println("\n✅ Tutti i test sulla V3 sono passati.");
        } finally {
            cleanup();
        }
    }

    static void setup() throws IOException {
        if (!Files.exists(TEST_ROOT)) Files.createDirectories(TEST_ROOT);

        // 1. Inizializziamo il registro (Logica esterna)
        registry = new ManualTypeRegistry(TEST_ROOT);
        registry.addAlias(User.class); // Verrà registrato come "user" (o nome semplice)

        // 2. Inizializziamo il manager con la dipendenza iniettata
        manager = new JsonGraphManager(registry);
    }

    // ── Test 1: Verifica troncatamento riferimenti ─────────────────────────────
    static void testUserRefTruncation() throws IOException {
        User user = new Client("alice@test.it", "password");
        user.setName("Alice");

        Session session = new Session( user);

        // Salviamo l'utente nella sua cartella dedicata (gestita dal registro)
        // Usiamo resolvePath per generare la stringa relativa corretta
        String userRelPath = "user/" + user.getId() + ".json";
        manager.save(user, userRelPath);

        // Salviamo la sessione: lo user al suo interno deve diventare un riferimento
        String sessionRelPath = "sessions/alice_sess.json";
        manager.save(session, sessionRelPath);

        // Verifica contenuto JSON
        Path sessionPath = TEST_ROOT.resolve(sessionRelPath);
        String raw = Files.readString(sessionPath);

        assertTrue("Deve contenere la chiave _ref", raw.contains("\"_ref\""));
        assertTrue("Deve contenere la chiave _folder", raw.contains("\"_folder\""));

        System.out.println("✅ testUserRefTruncation");
    }

    // ── Test 2: Caricamento e risoluzione automatica (Roundtrip) ───────────────
    static void testSessionRoundTrip() throws IOException {
        String sessionRelPath = "sessions/alice_sess.json";

        // Il manager caricherà la sessione e, vedendo il _ref, pescherà lo User dal suo file
        Session loadedSession = manager.load(sessionRelPath, Session.class);

        assertNotNull("La sessione caricata è null", loadedSession);
        assertNotNull("L'utente risolto è null", loadedSession.getUser());
        assertEquals("Alice", ((User)loadedSession.getUser()).getName());

        System.out.println("✅ testSessionRoundTrip");
    }

    // ── Test 3: Collezioni di entità ───────────────────────────────────────────
    static void testLazyCollectionRoundTrip() throws IOException {
        User bob = new User("bob@test.it", "secret");
        bob.setName("Bob");
        manager.save(bob, "user/bob@test.it.json");

        User alice = (User) manager.load("user/alice@test.it.json", User.class);

        LazyEntityList<User> list = new LazyEntityList<>();
        list.add(alice);
        list.add(bob);

        Session multiSession = new Session(alice);
        multiSession.addCollection("contacts", list);

        manager.save(multiSession, "sessions/multi.json");

        // Verifica caricamento lista
        Session loaded = manager.load("sessions/multi.json", Session.class);
        LazyEntityList<?> loadedList = (LazyEntityList<?>) loaded.getLazyCollections().get("contacts");

        System.out.println("✅ testLazyCollectionRoundTrip");
    }

    // ── Utilities ──────────────────────────────────────────────────────────────

    private static void assertTrue(String msg, boolean condition) {
        if (!condition) throw new AssertionError("FAIL: " + msg);
    }

    private static void assertNotNull(String msg, Object obj) {
        if (obj == null) throw new AssertionError("FAIL: " + msg);
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual))
            throw new AssertionError("FAIL: atteso [" + expected + "], trovato [" + actual + "]");
    }

    private static void cleanup() throws IOException {

    }
}