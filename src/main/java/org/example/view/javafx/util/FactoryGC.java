package org.example.view.javafx.util;

import org.example.view.javafx.componet.AbstractComponentGC;
import org.example.view.javafx.componet.HeaderGC;
import org.example.util.str.StrAppSystem;
import org.example.view.javafx.gc.*;

/**
 * Factory Centralizzata per l'istanziazione accoppiata dei GraphicController.
 * * Questa classe rispetta rigidamente il principio di Singola Responsabilità (SRP):
 * Alloca esclusivamente l'oggetto logico Java in memoria tramite l'operatore 'new'.
 * Non si occupa del parsing FXML, dell'iniezione dei nodi grafici o della gestione del contesto.
 */
public class FactoryGC {

    // Costruttore privato per impedire l'istanziazione di una classe puramente statica (Utility)
    private FactoryGC() {}

    /**
     * Crea e restituisce una nuova istanza di un GraphicController associato a una determinata rotta.
     * * @param <C> Il tipo specifico del GraphicController atteso dal chiamante.
     * @param route L'identificatore della rotta visiva (Enum ViewRoute).
     * @return L'istanza pura del controller logico fortemente tipizzata.
     * @throws IllegalArgumentException Se la rotta fornita è nulla.
     * @throws UnsupportedOperationException Se la rotta richiesta non è ancora stata implementata nel sistema.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GraphicController<C>> C create(ViewRoute route) {
        // Validazione formale dell'input per intercettare bug di programmazione a runtime
        if (route == null) {
            throw new IllegalArgumentException(StrAppSystem.get(StrAppSystem.ERR_CONTROLLER_NULL));
        }

        // Switch Expression per mappare in modo sicuro ogni elemento dell'enum alla rispettiva istanza Java
        return (C) switch (route) {

            // =========================================================================
            // --- ISTANZIAZIONE CONTROLLER DELLE PAGINE PRINCIPALI ---
            // =========================================================================
            case BOOKING -> new BookingGC();
            case HOME -> new HomeGC();
            case RENTAL -> new CatalogGC();
            case PRODUCT_DETAIL -> new ProductDetailGC();
            case SUCCESS -> new SuccessGC();
            case LOGIN -> new LoginGC();
            case REGISTER -> new RegisterGC();
            case USER_RENTAL -> new UserRentalGC();
            case SETTING -> new SettingGC();

            // Rotte strutturate nel routing ma i cui stubs logici non sono ancora pronti per la produzione
            case PROFILE -> throw new UnsupportedOperationException(route.getTitle()+StrAppSystem.get(StrAppSystem.ERR_FACTORY_GC));
            case ADD_ITEM -> throw new UnsupportedOperationException(route.getTitle()+StrAppSystem.get(StrAppSystem.ERR_FACTORY_GC));

            // =========================================================================
            // --- ISTANZIAZIONE CONTROLLER DEI COMPONENTI RIUTILIZZABILI ---
            // =========================================================================
            case HEADER -> new HeaderGC();

            // Corretto: La Factory fa solo la 'new' del controller logico.
            // Il caricamento visivo dell'FXML avverrà successivamente tramite il ciclo di vita del loader.
            case PRODUCT_CARD -> AbstractComponentGC.create(route);
            case RENTAL_CARD -> AbstractComponentGC.create(route);
        };
    }
}