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
     *
     */
    public static void loadDemoData() {
        EntityDAO<Product> managerP=DAOManager.getEntity(EntityType.PRODUCT);
        EntityDAO<User> managerU=DAOManager.getEntity(EntityType.USER);
        EntityDAO<Session> managerS=DAOManager.getEntity(EntityType.SESSION);

        logger.log(Level.INFO, "Caricamento dati demo nel catalogo noleggi...");

        // 1. Generazione e salvataggio dei prodotti demo
        Product p1 = createMacBook();
        Product p2 = createCamera();
        Product p3 = createScooter();

        managerP.save( p1);
        managerP.save( p2);
        managerP.save( p3);

        // 2. Generazione dell'attore (User/Owner) e della relativa sessione operativa
        ActorAbstractFactory strategyFactory = ActorFactory.getFactory(ActorEnumFactory.STRATEGY);
        UserStrategy mockOwner = (UserStrategy) strategyFactory.createOwner(OWNER_EMAIL, OWNER_EMAIL);
        managerU.save( mockOwner);

        Session mockSession = new Session(mockOwner);
        mockOwner.execute(mockSession, p1);
        mockOwner.execute(mockSession, p2);
        mockOwner.execute(mockSession, p3);

        managerS.save(mockSession);

        logger.log(Level.INFO, "Dati demo caricati con successo! Prodotti aggiunti: 3.");
    }

    private static Product createMacBook() {
        Product p = new Product(OWNER_EMAIL, "MacBook Pro 16 M3", 25.00);
        p.setCategory("Informatica");
        p.setDescription("Laptop professionale per sviluppo software, montaggio video e grafica. Stato impeccabile.");
        p.setAvailable(true);
        p.setImageUrl("macbook_pro.png");

        List<String> specs = new ArrayList<>();
        specs.add("CPU Apple M3 Pro 12-core");
        specs.add("36GB Memoria Unificata");
        specs.add("512GB SSD ultra-rapido");
        specs.add("Display Liquid Retina XDR 16\"");
        p.setSpecifications(specs);

        List<String> cond = new ArrayList<>();
        cond.add("Ritiro a mano previo controllo congiunto");
        cond.add("Deposito cauzionale di 200€ all'atto del ritiro");
        cond.add("Restituzione comprensiva di alimentatore originale e custodia");
        p.setRentalConditions(cond);
        return p;
    }

    private static Product createCamera() {
        Product p = new Product(OWNER_EMAIL, "Fotocamera Sony Alpha 7 IV", 40.00);
        p.setCategory("Fotografia");
        p.setDescription("Corpo macchina mirrorless full-frame da 33 MP. Ideale per eventi e content creation.");
        p.setAvailable(true);
        p.setImageUrl("sony_a7iv.png");

        List<String> specs = new ArrayList<>();
        specs.add("Sensore Full-Frame Exmor R da 33 MP");
        specs.add("Registrazione video 4K 60p");
        specs.add("Autofocus Real-time in tempo reale per occhi");
        specs.add("Doppio slot per schede SD/CFexpress");
        p.setSpecifications(specs);

        List<String> cond = new ArrayList<>();
        cond.add("Fornito con 2 batterie cariche e caricabatterie");
        cond.add("Obiettivo non incluso (noleggiabile separatamente)");
        cond.add("È richiesta la firma del modulo di responsabilità per danni");
        p.setRentalConditions(cond);
        return p;
    }

    private static Product createScooter() {
        Product p = new Product(OWNER_EMAIL, "Monopattino Elettrico Xiaomi Ultra", 12.50);
        p.setCategory("Mobilità");
        p.setDescription("Monopattino elettrico con doppia sospensione. Perfetto per muoversi in centro città.");
        p.setAvailable(false);
        p.setImageUrl("xiaomi_ultra.png");

        List<String> specs = new ArrayList<>();
        specs.add("Autonomia fino a 70 km");
        specs.add("Motore con potenza massima di 940W");
        specs.add("Sistema di doppia sospensione anteriore e posteriore");
        specs.add("Pneumatici da 10 pollici autosigillanti");
        p.setSpecifications(specs);

        List<String> cond = new ArrayList<>();
        cond.add("Uso obbligatorio del casco (fornito gratuitamente su richiesta)");
        cond.add("Riconsegna con batteria caricata ad almeno il 20%");
        cond.add("Noleggio consentito solo a maggiorenni");
        p.setRentalConditions(cond);
        return p;
    }
}