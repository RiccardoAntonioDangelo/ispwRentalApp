package org.example.model.dao.proxy.test;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.entity.actors.User;
import org.example.model.entity.actors.simple.Client;

import java.util.List;

public class ObserverArchitectureDeepTest {

    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("   DEEP TEST: ARCHITETTURA REATTIVA & CACHING PROXY    ");
        System.out.println("=======================================================\n");

        // Inizializzazione della catena completa
        DAOManager.initializeSingleton(EnumDaoType.FILE, true);
        UserDAO userDAO = DAOManager.getUserDAO();

        System.out.println("[INFO] Catena Proxy inizializzata correttamente.\n");

        try {
            // Setup dati di test
            String email = "deep_test_reactive@example.com";
            Client client = new Client(email, "pass123");
            client.setName("Versione Iniziale");

            // --- TEST 1: SALVATAGGIO INIZIALE ---
            printSection("TEST 1: Persistenza Iniziale e Iniezione Observer");
            long start = System.nanoTime();
            boolean isSaved = userDAO.save(client);
            long end = System.nanoTime();
            long timeSaveInitial = end - start;
            assertCondition(isSaved, "Salvataggio iniziale fallito.");
            System.out.printf("-> Oggetto salvato sul file. Tempo impiegato: %,d ns\n", timeSaveInitial);

            // --- TEST 2: VERIFICA IMPATTO CACHE (CONFRONTO TEMPI) ---
            printSection("TEST 2: Verifica Performance Cache (Hit vs Miss)");

            // Primo recupero: va a leggere dal supporto fisico (o logica interna del DAO reale)
            start = System.nanoTime();
            User missUser = userDAO.getById(email);
            long timeMiss = System.nanoTime() - start;
            System.out.printf("-> 1° Recupero (Cache Miss/Caricamento + Attach): %,d ns [Nome: %s]\n", timeMiss, missUser.getName());

            // Secondo recupero: DEVE prenderlo dalla mappa in memoria del CachedDAOProxy
            start = System.nanoTime();
            User hitUser = userDAO.getById(email);
            long timeHit = System.nanoTime() - start;
            System.out.printf("-> 2° Recupero (Cache Hit di conferma): %,d ns [Nome: %s]\n", timeHit, hitUser.getName());

            assertCondition(timeHit < timeMiss, "La cache non sta ottimizzando i tempi di lettura!");
            System.out.printf("   [GUADAGNO CACHE]: La cache hit è %.2fx più veloce.\n", (double) timeMiss / timeHit);

            // --- TEST 3: REATTIVITÀ E AUTO-PERSISTENZA AUTOMATICA ---
            printSection("TEST 3: Trigger Reattivo su Mutazione Campo (Auto-Save)");
            System.out.println("-> Esecuzione di hitUser.setName()...");

            String targetNome = "Nome Aggiornato Automaticamente v2";

            start = System.nanoTime();
            hitUser.setName(targetNome); // Scatena commitChange() -> ObserverProxy -> realDAO.save() e aggiornamento cache
            long timeAutoSave = System.nanoTime() - start;

            System.out.printf("-> Tempo impiegato per l'intera catena di auto-salvataggio: %,d ns\n", timeAutoSave);

            // --- TEST 4: VERIFICA COERENZA DEI DATI REALI ---
            printSection("TEST 4: Verifica Integrità e Coerenza tra i Livelli");

            // Riaffacciamoci sul DAO per riprendere l'entità e vedere se ha memorizzato la modifica del test 3
            User verifiedUser = userDAO.getById(email);
            System.out.println("-> Nome estratto dal DAO dopo l'auto-salvataggio: " + verifiedUser.getName());

            assertCondition(targetNome.equals(verifiedUser.getName()),
                    "I dati non coincidono! L'auto-salvataggio ha fallito o la cache è disallineata.");
            System.out.println("-> COERENZA OK: I dati letti riflettono la modifica reattiva.");

            // --- TEST 5: METODI MASSIVI (getAll) ---
            printSection("TEST 5: Verifica di getAll() e re-iniezione su liste");
            List<User> allUsers = userDAO.getAll();
            System.out.printf("-> Elementi totali trovati nel database/file: %d\n", allUsers.size());

            // Prendiamo un utente qualsiasi dalla lista e testiamo se il getAll gli ha ri-attaccato l'observer
            User userFromList = allUsers.stream()
                    .filter(u -> u.getEmail().equals(email))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Utente di test sparito nel getAll!"));

            String nomeDaLista = "Modificato Da Lista";
            userFromList.setName(nomeDaLista);

            // Verifica finale di persistenza dopo modifica da lista
            assertCondition(nomeDaLista.equals(userDAO.getById(email).getName()),
                    "L'observer non è stato iniettato correttamente sugli elementi restituiti da getAll()!");
            System.out.println("-> RE-INIEZIONE LISTE OK: Anche gli elementi di getAll() sono reattivi.");

            // Pulizia finale (Opzionale, decommenta se vuoi ripulire il DB/File a fine test)
            // userDAO.delete(email);

            printReportFinale(timeSaveInitial, timeMiss, timeHit, timeAutoSave);

        } catch (Exception e) {
            System.err.println("\n[!!!] FALLIMENTO CRITICO DELL'ARCHITETTURA [!!!]");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printSection(String title) {
        System.out.println("\n-------------------------------------------------------");
        System.out.println(" " + title);
        System.out.println("-------------------------------------------------------");
    }

    private static void printReportFinale(long tSave, long tMiss, long tHit, long tAuto) {
        System.out.println("\n=======================================================");
        System.out.println("             REPORT FINALE DEI CONFRONTI               ");
        System.out.println("=======================================================");
        System.out.printf("1. Salvataggio Manuale Esplicito:      %,12d ns\n", tSave);
        System.out.printf("2. Lettura con Caricamento Fisico:     %,12d ns\n", tMiss);
        System.out.printf("3. Lettura da Cache di Memoria:        %,12d ns\n", tHit);
        System.out.printf("4. Auto-Salvataggio da Setter (Mutazione): %,12d ns\n", tAuto);
        System.out.println("=======================================================");
        System.out.println(" L'architettura è stabile, fully-decoupled e conforme.");
        System.out.println("=======================================================");
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            System.err.println("\n[ASSERTION FAILED]: " + message);
            throw new RuntimeException("Test Architecture Error: " + message);
        }
    }
}