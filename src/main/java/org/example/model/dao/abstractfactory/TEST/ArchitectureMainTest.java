package org.example.model.dao.abstractfactory.TEST;

import org.example.model.dao.abstractfactory.*;
import org.example.model.dao.proxy.cache.CachedDAOFactoryProxy;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.actors.User;

public class ArchitectureMainTest {

    public static void main(String[] args) {
        System.out.println("=== INIZIO TEST ARCHITETTURA DAO ===\n");

        // 1. TEST VERSIONE DEMO (In Memoria con Proxy)
        testFactory(EnumDaoType.DEMO, "Ambiente DEMO");

        System.out.println("\n------------------------------------\n");

        // 2. TEST VERSIONE FILE (JSON con FileBaseDAO)
        testFactory(EnumDaoType.FILE, "Ambiente FILE (JSON)");

        System.out.println("\n------------------------------------\n");

        // 3. TEST VERSIONE dbms (dbms con FileBaseDAO)
        testFactory(EnumDaoType.DBMS, "Ambiente dbms (dbms)");

        System.out.println("\n=== TUTTI I TEST COMPLETATI CON SUCCESSO ===");
    }

    private static void testFactory(EnumDaoType type, String description) {
        System.out.println(">>> Testando: " + description);
        
        try {
            // Otteniamo la factory specifica
           // DAOFactory factory = DAOFactory.getDAOFactory(type);
            DAOFactory factory = CachedDAOFactoryProxy.getDAOFactory(type);
            UserDAO userDAO = factory.getUserDAO();

            // Creiamo un Client (Sottoclasse di User)
            String email = "test_" + type.toString().toLowerCase() + "@example.com";
            Client nuovoClient = new Client(email, "password123");
            nuovoClient.setName("Mario Rossi " + type);

            // --- TEST SAVE ---
            System.out.print("Salvataggio entità... ");
            boolean saved = userDAO.save(nuovoClient);
            assertCondition(saved, "Errore durante il salvataggio!");
            System.out.println("OK.");

            // --- TEST GET BY ID (Polimorfismo) ---
            System.out.print("Recupero entità per ID... ");
            User retrieved = userDAO.getById(email);
            assertCondition(retrieved != null, "Entità non trovata!");
            assertCondition(retrieved instanceof Client, "L'entità recuperata non è un Client!");
            assertCondition(retrieved.getName().contains("Mario Rossi"), "Dati corrotti nel recupero!");
            System.out.println("OK. (Recuperato: " + retrieved.getClass().getSimpleName() + ")");

            // --- TEST DELETE ---
            System.out.print("Eliminazione entità... ");
            boolean deleted = userDAO.delete(email);
            assertCondition(deleted, "Eliminazione fallita!");
            
            User afterDelete = userDAO.getById(email);
            assertCondition(afterDelete == null, "L'entità esiste ancora dopo la delete!");
            System.out.println("OK.");

        } catch (Exception e) {
            System.err.println("\n!!! FALLIMENTO TEST " + description + " !!!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Assertion Failed: " + message);
        }
    }
}