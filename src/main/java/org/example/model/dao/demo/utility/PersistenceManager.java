package org.example.model.dao.demo.utility;

import org.example.model.entity.actors.factory.ActorAbstractFactory;
import org.example.model.entity.actors.factory.ActorEnumFactory;
import org.example.model.entity.actors.factory.ActorFactory;
import org.example.model.entity.actors.strategy.UserStrategy;
import org.example.model.entity.product.Product;
import org.example.model.entity.session.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceManager {

    // Storage raggruppato per classe root
    protected static final Map<Class<?>, Map<String, Object>> storage = new ConcurrentHashMap<>();

    public PersistenceManager() {
        loadDemoData();
    }

    /**
     * METODO CHIAVE PER IL PROXY:
     * Restituisce la classe che funge da "cartella" (bucket) per lo storage.
     * Di default è la classe stessa dell'oggetto.
     */
    protected Class<?> getStorageBucket(Class<?> clazz) {
        return clazz;
    }

    public void save(String id, Object entity) {
        if (entity == null || id == null) return;

        // Determiniamo in quale "cartella" salvare
        Class<?> bucket = getStorageBucket(entity.getClass());

        storage.computeIfAbsent(bucket, k -> new ConcurrentHashMap<>())
                .put(id, entity);
    }

    public boolean delete(Class<?> clazz, String id) {
        if (id == null) return false;

        Class<?> bucket = getStorageBucket(clazz);
        Map<String, Object> classMap = storage.get(bucket);

        return classMap != null && classMap.remove(id) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String id) {
        if (id == null) return null;

        Class<?> bucket = getStorageBucket(clazz);
        Map<String, Object> classMap = storage.get(bucket);

        return (classMap != null) ? (T) classMap.get(id) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> clazz) {
        Class<?> bucket = getStorageBucket(clazz);
        Map<String, Object> classMap = storage.get(bucket);

        if (classMap == null) return new ArrayList<>();

        // Filtro di sicurezza per restituire solo istanze della classe richiesta
        List<T> results = new ArrayList<>();
        for (Object obj : classMap.values()) {
            if (clazz.isInstance(obj)) {
                results.add((T) obj);
            }
        }
        return results;
    }

    protected void loadDemoData() {
        //todo
        System.out.println("Caricamento dati demo nel catalogo noleggi...");

        // Usiamo una costante per l'email del nostro Owner mock
        final String OWNER_EMAIL = "aaa@aaa.aaa";

        // =========================================================================
        // ARTICOLO 1: Tech / Informatica (Proprietario: aaa@aaa.aaa)
        // =========================================================================
        Product p1 = new Product(OWNER_EMAIL, "MacBook Pro 16 M3", 25.00);
        p1.setCategory("Informatica");
        p1.setDescription("Laptop professionale per sviluppo software, montaggio video e grafica. Stato impeccabile.");
        p1.setAvailable(true);
        p1.setImageUrl("macbook_pro.png");

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
        // ARTICOLO 2: Fotografia / Video (Proprietario: aaa@aaa.aaa)
        // =========================================================================
        Product p2 = new Product(OWNER_EMAIL, "Fotocamera Sony Alpha 7 IV", 40.00);
        p2.setCategory("Fotografia");
        p2.setDescription("Corpo macchina mirrorless full-frame da 33 MP. Ideale per eventi, matrimoni e content creation.");
        p2.setAvailable(true);
        p2.setImageUrl("sony_a7iv.png");

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
        // ARTICOLO 3: Mobilità / Outdoor (Proprietario: aaa@aaa.aaa)
        // =========================================================================
        Product p3 = new Product(OWNER_EMAIL, "Monopattino Elettrico Xiaomi Ultra", 12.50);
        p3.setCategory("Mobilità");
        p3.setDescription("Monopattino elettrico con doppia sospensione. Perfetto per muoversi agilmente in centro città.");
        p3.setAvailable(false);
        p3.setImageUrl("xiaomi_ultra.png");

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
        // PERSISTENZA DEI DATI DEI PRODOTTI
        // =========================================================================
        save(p1.getId(), p1);
        save(p2.getId(), p2);
        save(p3.getId(), p3);

        // =========================================================================
        // MOCK UTENTE (OWNER/CLIENT) E STRUTTURA DI SESSIONE
        // =========================================================================

        // Creiamo l'utente che fa sia da Client che da Owner (istanza di Owner)
        // Usiamo l'email coerente con i prodotti estratti sopra
        ActorAbstractFactory strategyFactory = ActorFactory.getFactory(ActorEnumFactory.STRATEGY);
        UserStrategy mockOwner =(UserStrategy) strategyFactory.createOwner(OWNER_EMAIL, OWNER_EMAIL);

        // Salviamo l'anagrafica utente nel persistence manager
        save(mockOwner.getEmail(), mockOwner);

        // Creiamo la sessione operativa
        Session mockSession = new Session(mockOwner);
        mockOwner.execute(mockSession,p1);
        mockOwner.execute(mockSession,p2);
        mockOwner.execute(mockSession,p3);

        // 3. Salviamo la sessione nel PersistenceManager usando il suo ID univoco
        save(mockSession.getId(), mockSession);

        System.out.println("Dati demo caricati con successo!");
        System.out.println(" -> Prodotti aggiunti nel sistema: 3");

        System.out.println("Dati demo caricati con successo! Prodotti aggiunti: 3.");
    }
}