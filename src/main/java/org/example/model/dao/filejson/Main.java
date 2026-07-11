package org.example.model.dao.filejson;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.dao.filejson.dao.FileJsonDAOFactory;
import org.example.model.entity.actors.User;
import org.example.model.entity.actors.strategy.UserStrategy;
import org.example.model.entity.actors.factory.StrategyActorFactory;
import org.example.model.entity.product.Product;
import org.example.model.entity.session.Session;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INIZIO TEST ARCHITETTURA DAO FACTORY (JSON) ===");

        // Inizializziamo il gestore del DAO Manager in modalità FILE
        DAOManager.initializeSingleton(EnumDaoType.FILE, true);

        // 1. Istanziamo la Factory concreta per i File JSON
        FileJsonDAOFactory factory = new FileJsonDAOFactory();

        // -----------------------------------------------------------------------------------
        // INTERVENTO DI CONFIGURAZIONE SERIALIZZATORE
        // -----------------------------------------------------------------------------------
        Path baseDir = Paths.get("database_data");
        SmartJsonSerializer serializer = new SmartJsonSerializer(baseDir);
        // -----------------------------------------------------------------------------------

        // 2. Otteniamo i DAO specifici dalla Factory
        UserDAO userDAO = factory.getUserDAO();
        ProductDAO productDAO = factory.getProductDAO();
        SessionDAO sessionDAO = factory.getSessionDAO();


        System.out.println("\n--- FASE 1: CREAZIONE E SALVATAGGIO DEI DATI DEMO ---");

        // Costante per l'email dell'Owner mock (versione file)
        final String OWNER_EMAIL_FILE = "aaa@aaa.aaa";

        // =========================================================================
        // ARTICOLO 1: Tech / Informatica (File Version)
        // =========================================================================
        Product p1 = new Product(OWNER_EMAIL_FILE, "MacBook Pro 16 M3 File", 25.00);
        p1.setCategory("Informatica");
        p1.setDescription("Laptop professionale per sviluppo software, montaggio video e grafica. Stato impeccabile. [File Storage]");
        p1.setAvailable(true);
        p1.setImageUrl("macbook_pro_file.png");

        List<String> specs1 = new ArrayList<>();
        specs1.add("CPU Apple M3 Pro 12-core");
        specs1.add("36GB Memoria Unificata");
        specs1.add("512GB SSD ultra-rapido");
        specs1.add("Display Liquid Retina XDR 16\"");
        p1.setSpecifications(specs1);

        List<String> cond1 = new ArrayList<>();
        cond1.add("Ritiro a mano previo controllo congiunto");
        cond1.add("Deposito cauzionale di 200€ all'atto del ritiro");
        cond1.add("Restituzione comprensiva di alimentatore originale e custodia");
        p1.setRentalConditions(cond1);

        // =========================================================================
        // ARTICOLO 2: Fotografia / Video (File Version)
        // =========================================================================
        Product p2 = new Product(OWNER_EMAIL_FILE, "Fotocamera Sony Alpha 7 IV File", 40.00);
        p2.setCategory("Fotografia");
        p2.setDescription("Corpo macchina mirrorless full-frame da 33 MP. Ideale per eventi e content creation. [File Storage]");
        p2.setAvailable(true);
        p2.setImageUrl("sony_a7iv_file.png");

        List<String> specs2 = new ArrayList<>();
        specs2.add("Sensore Full-Frame Exmor R da 33 MP");
        specs2.add("Registrazione video 4K 60p");
        specs2.add("Autofocus Real-time in tempo reale per occhi");
        specs2.add("Doppio slot per schede SD/CFexpress");
        p2.setSpecifications(specs2);

        List<String> cond2 = new ArrayList<>();
        cond2.add("Fornito con 2 batterie cariche e caricabatterie");
        cond2.add("Obiettivo non incluso (noleggiabile separatamente)");
        cond2.add("È richiesta la firma del modulo di responsabilità per danni");
        p2.setRentalConditions(cond2);

        // =========================================================================
        // ARTICOLO 3: Mobilità / Outdoor (File Version)
        // =========================================================================
        Product p3 = new Product(OWNER_EMAIL_FILE, "Monopattino Elettrico Xiaomi Ultra File", 12.50);
        p3.setCategory("Mobilità");
        p3.setDescription("Monopattino elettrico con doppia sospensione. Perfetto per muoversi in centro. [File Storage]");
        p3.setAvailable(false);
        p3.setImageUrl("xiaomi_ultra_file.png");

        List<String> specs3 = new ArrayList<>();
        specs3.add("Autonomia fino a 70 km");
        specs3.add("Motore con potenza massima di 940W");
        specs3.add("Sistema di doppia sospensione anteriore e posteriore");
        specs3.add("Pneumatici da 10 pollici autosigillanti");
        p3.setSpecifications(specs3);

        List<String> cond3 = new ArrayList<>();
        cond3.add("Uso obbligatorio del casco (fornito gratuitamente su richiesta)");
        cond3.add("Riconsegna con batteria caricata ad almeno il 20%");
        cond3.add("Noleggio consentito solo a maggiorenni");
        p3.setRentalConditions(cond3);

        // =========================================================================
        // CREAZIONE UTENTE MOCK E STRATEGIA DI ESECUZIONE (File Version)
        // =========================================================================
        // Generiamo il mockOwner usando la factory corretta per le strategie
        User mockOwnerFile = (new StrategyActorFactory()).createOwner(OWNER_EMAIL_FILE, OWNER_EMAIL_FILE);
        Session session=new Session(mockOwnerFile);
        // Eseguiamo i comandi di sessione per popolare eventuali logiche comportamentali interne
        // Nota: Nel tuo PersistenceManager passavi un oggetto Session mock, qui simuliamo lo stesso comportamento
        // impostando i vincoli se la tua architettura Strategy lo prevede prima del salvataggio.

        // Salvataggio effettivo su disco tramite DAO
        session.execute(p1);
        session.execute(p3);
        session.execute(p2);

        boolean userSaved = sessionDAO.save(session);

        System.out.println("Utente Proprietario File salvato? " + userSaved);
        System.out.println("-> Controlla la cartella 'database_data/' nel tuo progetto per vedere i file JSON!");

        System.out.println("\n--- FASE 2: RECUPERO UTENTE TRAMITE ID ---");

        // Recuperiamo l'utente specifico appena memorizzato
        Session utenteRecuperato = sessionDAO.getById(mockOwnerFile.getId());

        if (utenteRecuperato != null) {
            System.out.println("Utente recuperato dal file JSON con successo!");
            System.out.println("  ID: " + utenteRecuperato.getId());
            System.out.println("  Email: " + utenteRecuperato.getUser());


        } else {
            System.err.println("Errore: Impossibile recuperare l'utente dal file.");
        }

        System.out.println("\n--- FASE 3: RECUPERO DI TUTTI I PRODOTTI REGISTRATI (getAll) ---");

        // Eseguiamo una scansione leggendo tutti i file nella sottocartella dei prodotti
        List<Product> tuttiIProdotti = productDAO.getAll();
        System.out.println("Numero di prodotti trovati nella cartella: " + tuttiIProdotti.size());
        for (Product p : tuttiIProdotti) {
            System.out.println("  - [" + p.getId() + "] " + p.getName() + " (" + p.getDailyPrice() + "€) - Cat: " + p.getCategory());
        }

        System.out.println("\n=== FINE TEST ARCHITETTURA ===");
    }
}