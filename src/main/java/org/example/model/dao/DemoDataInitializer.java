package org.example.model.dao;

import org.example.model.dao.abstractfactory.EntityDAO;
import org.example.model.dao.abstractfactory.EntityType;
import org.example.model.entity.actors.User;
import org.example.model.entity.actors.factory.ActorAbstractFactory;
import org.example.model.entity.actors.factory.ActorEnumFactory;
import org.example.model.entity.actors.factory.ActorFactory;
import org.example.model.entity.actors.strategy.UserStrategy;
import org.example.model.entity.product.Product;
import org.example.model.entity.session.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe ausiliaria responsabile dell'inizializzazione e del caricamento dei dati Mock (Demo)
 * all'interno del sistema di persistenza in memoria.
 */
public final class DemoDataInitializer {

    private static final Logger logger = Logger.getLogger(DemoDataInitializer.class.getName());
    private static final String OWNER_EMAIL = "aaa@aaa.aaa";

    // Costruttore privato per prevenire l'istanza di una classe di utilità pura
    private DemoDataInitializer() {
        throw new UnsupportedOperationException("Questa è una classe di utilità e non può essere istanziata.");
    }

    /**
     * Carica i prodotti e l'utente demo all'interno del gestore di persistenza fornito.
     */
    public static void loadDemoData() {
        EntityDAO<Product> managerP = DAOManager.getEntity(EntityType.PRODUCT);
        EntityDAO<User> managerU = DAOManager.getEntity(EntityType.USER);
        EntityDAO<Session> managerS = DAOManager.getEntity(EntityType.SESSION);

        logger.log(Level.INFO, "Caricamento dati demo nel catalogo noleggi...");

        // 1. Generazione e salvataggio dei prodotti demo tramite metodi factory dedicati
        Product p1 = createMacBook();
        Product p2 = createCamera();
        Product p3 = createScooter();

        managerP.save(p1);
        managerP.save(p2);
        managerP.save(p3);

        // 2. Generazione dell'attore (User/Owner) e della relativa sessione operativa
        ActorAbstractFactory strategyFactory = ActorFactory.getFactory(ActorEnumFactory.STRATEGY);
        UserStrategy mockOwner = (UserStrategy) strategyFactory.createOwner(OWNER_EMAIL, OWNER_EMAIL);
        managerU.save(mockOwner);

        Session mockSession = new Session(mockOwner);
        mockOwner.execute(mockSession, p1);
        mockOwner.execute(mockSession, p2);
        mockOwner.execute(mockSession, p3);

        managerS.save(mockSession);

        logger.log(Level.INFO, "Dati demo caricati con successo! Prodotti aggiunti: 3.");
    }

    /**
     * 🆕 Metodo unico parametrizzato per centralizzare la creazione dei prodotti.
     */
    private static Product createProduct(String title, double price, String category, String description,
                                         boolean isAvailable,
                                         List<String> specifications, List<String> conditions) {
        Product p = new Product(OWNER_EMAIL, title, price);
        p.setCategory(category);
        p.setDescription(description);
        p.setAvailable(isAvailable);
        p.setSpecifications(new ArrayList<>(specifications));
        p.setRentalConditions(new ArrayList<>(conditions));
        return p;
    }

    private static Product createMacBook() {
        return createProduct(
                "MacBook Pro 16 M3",
                25.00,
                "Informatica",
                "Laptop professionale per sviluppo software, montaggio video e grafica. Stato impeccabile.",
                true,
                Arrays.asList("CPU Apple M3 Pro 12-core", "36GB Memoria Unificata", "512GB SSD ultra-rapido", "Display Liquid Retina XDR 16\""),
                Arrays.asList("Ritiro a mano previo controllo congiunto", "Deposito cauzionale di 200€ all'atto del ritiro", "Restituzione comprensiva di alimentatore originale e custodia")
        );
    }

    private static Product createCamera() {
        return createProduct(
                "Fotocamera Sony Alpha 7 IV",
                40.00,
                "Fotografia",
                "Corpo macchina mirrorless full-frame da 33 MP. Ideale per eventi e content creation.",
                true,
                Arrays.asList("Sensore Full-Frame Exmor R da 33 MP", "Registrazione video 4K 60p", "Autofocus Real-time in tempo reale per occhi", "Doppio slot per schede SD/CFexpress"),
                Arrays.asList("Fornito con 2 batterie cariche e caricabatterie", "Obiettivo non incluso (noleggiabile separatamente)", "È richiesta la firma del modulo di responsabilità per danni")
        );
    }

    private static Product createScooter() {
        return createProduct(
                "Monopattino Elettrico Xiaomi Ultra",
                12.50,
                "Mobilità",
                "Monopattino elettrico con doppia sospensione. Perfetto per muoversi in centro città.",
                false,
                Arrays.asList("Autonomia fino a 70 km", "Motore con potenza massima di 940W", "Sistema di doppia sospensione anteriore e posteriore", "Pneumatici da 10 pollici autosigillanti"),
                Arrays.asList("Uso obbligatorio del casco (fornito gratuitamente su richiesta)", "Riconsegna con batteria caricata ad almeno il 20%", "Noleggio consentito solo a maggiorenni")
        );
    }
}