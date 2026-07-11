package org.example.view.javafx.util;

import org.example.util.str.StrApp;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Definisce il sistema di routing centralizzato dell'applicazione JavaFX.
 * Mappa ogni vista (pagine e componenti) al rispettivo file FXML, titolo e requisiti di autenticazione.
 */
public enum ViewRoute {

    // =========================================================================
    // --- SCHERMATE / PAGINE PRINCIPALI ---
    // =========================================================================

    /** Schermata principale di benvenuto. */
    HOME("home_view.fxml", StrApp.NAV_HOME, false),

    /** Catalogo dei prodotti disponibili per il noleggio. */
    RENTAL("rental_view.fxml", StrApp.NAV_RENTAL, false),

    /** Dettaglio tecnico e informativo di un singolo prodotto. */
    PRODUCT_DETAIL("product_detail_view.fxml", StrApp.NAV_PRODUCT_DETAIL, false),

    /** Modulo per effettuare la prenotazione di un noleggio. Richiede login. */
    BOOKING("booking_view.fxml", StrApp.NAV_BOOKING_FORM, true),

    /** Schermata di conferma a seguito di un'operazione andata a buon fine. */
    SUCCESS("success_view.fxml", StrApp.NAV_SUCCESS, false),

    /** Form per l'inserimento di un nuovo articolo da parte del proprietario. Richiede login. */
    ADD_ITEM("add_item_view.fxml", StrApp.NAV_ADD_ITEM, true),

    /** Pagina di login/autenticazione utente. */
    LOGIN("login_view.fxml", StrApp.NAV_LOGIN, false),

    /** Pagina per la registrazione di nuovi account (Clienti o Proprietari). */
    REGISTER("register_view.fxml", StrApp.NAV_REGISTER, false),

    /** Pannello di gestione del profilo personale dell'utente loggato. Richiede login. */
    PROFILE("profile_view.fxml", StrApp.NAV_PROFILE, true),

    /** Pagina delle impostazioni dell'applicazione (es. cambio tema). */
    SETTING("setting_view.fxml", StrApp.NAV_SETTING, false),

    /** Area di gestione e storico dei noleggi dell'utente. Richiede login. */
    USER_RENTAL("user_rental_view.fxml", StrApp.NAV_USER_RENTAL, true),

    // =========================================================================
    // --- COMPONENTI GRAFICI RIUTILIZZABILI ---
    // =========================================================================

    /** Barra di navigazione superiore (Navbar). */
    HEADER("header_view.fxml", StrApp.NAV_HEADER, false),

    /** Card per la visualizzazione sintetica di un prodotto nel catalogo. */
    PRODUCT_CARD("product_card_view.fxml", StrApp.NAV_PRODUCT_CARD, false),

    /** Card per la visualizzazione e interazione con un singolo noleggio. */
    RENTAL_CARD("rental_card_view.fxml", StrApp.NAV_RENTAL_CARD, true);


    // =========================================================================
    // --- CONFIGURAZIONE E STRUTTURE DATI DI SUPPORTO ---
    // =========================================================================

    /** Mappa di caching per risolvere velocemente una rotta partendo dal titolo. */
    private static final Map<String, ViewRoute> TITLE_MAP = new HashMap<>();

    /** Mappa di caching per risolvere velocemente una rotta partendo dal path FXML completo. */
    private static final Map<String, ViewRoute> PATH_MAP = new HashMap<>();

    /** Percorso base nel classpath dove risiedono i file di configurazione grafica FXML. */
    private static final String BASE_PATH = "org/example/fxml/";

    // Blocco di inizializzazione statica per popolare le mappe di lookup/cache
    static {
        for (ViewRoute route : values()) {
            TITLE_MAP.put(route.title, route);
            PATH_MAP.put(route.getFxmlPath(), route);
        }
    }

    /** Nome del file fisico FXML (es. "home_view.fxml"). */
    private final String fxmlFileName;

    /** Titolo della vista o etichetta di navigazione associata. */
    private final String title;

    /** Flag che indica se la rotta è protetta ed accessibile solo previo login. */
    private final boolean requiresAuth;

    /**
     * Costruttore interno dell'enum per configurare le proprietà di ciascuna rotta.
     * * @param fxmlFileName Nome del file FXML.
     * @param title Nome o etichetta della vista definita in StrApp.
     * @param requiresAuth True se richiede autorizzazione, false altrimenti.
     */
    ViewRoute(String fxmlFileName, String title, boolean requiresAuth) {
        this.fxmlFileName = fxmlFileName;
        this.title = title;
        this.requiresAuth = requiresAuth;
    }

    // =========================================================================
    // --- METODI PUBLICI DI UTILITY E COMPORTAMENTO ---
    // =========================================================================

    /**
     * Costruisce e restituisce il percorso assoluto nel classpath del file FXML.
     * * @return Stringa del percorso completo (es. "/org/example/fxml/home_view.fxml").
     */
    public String getFxmlPath() {
        return "/" + BASE_PATH + fxmlFileName;
    }

    /**
     * Ritorna il titolo testuale della rotta.
     */

    public String getTitle() {
        return title;
    }

    /**
     * Converte il percorso stringa in un oggetto risorsa URL valido tramite il ViewLoader.
     * Pronto per essere passato all'FXMLLoader di JavaFX.
     * * @return Oggetto URL della risorsa FXML trovato nel classpath.
     */
    public URL getUrl() {
        return ViewLoader.stringToUrl(this.getFxmlPath());
    }

    /**
     * Sfrutta la factory dedicata per istanziare e accoppiare il GraphicController
     * specifico associato a questa rotta.
     * * @param <C> Il tipo specifico del GraphicController atteso.
     * @return L'istanza del GraphicController associato fortemente tipizzata.
     */
    public <C extends GraphicController<C>> GraphicController<C> getGraphicController() {
        return FactoryGC.create(this);
    }

    /**
     * Controlla se l'accesso a questa specifica vista richiede una sessione utente attiva.
     */
    public boolean isAuthRequired() {
        return requiresAuth;
    }

    /**
     * Verifica se la rotta rappresenta una schermata principale autonoma (Pagina)
     * o un frammento/componente innestato (es. l'Header).
     */
    public boolean isPage() {
        return this != HEADER;
    }

    /**
     * Cerca e restituisce la rotta corrispondente al titolo fornito.
     * * @param title Il titolo da cercare.
     * @return La ViewRoute corrispondente, o null se non trovata.
     */
    public static ViewRoute fromTitle(String title) {
        return TITLE_MAP.get(title);
    }

    /**
     * Cerca e restituisce la rotta corrispondente al percorso FXML fornito.
     * * @param path Il percorso FXML completo (es. "/org/example/fxml/home_view.fxml").
     * @return La ViewRoute corrispondente, o null se non trovata.
     */
    public static ViewRoute fromPath(String path) {
        return PATH_MAP.get(path);
    }
}