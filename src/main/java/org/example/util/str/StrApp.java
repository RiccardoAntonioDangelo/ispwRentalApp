package org.example.util.str;

/**
 * Classe per la gestione centralizzata delle stringhe (I18n simulato).
 */
public class StrApp {


    private StrApp() {}
    public static final String LOGO_MAIN_EMOJI ="💻";

    // Chiavi per la header
    public static final String LOGO_MAIN_TITLE ="Noleggio" ;
    public static final String LOGO_SUBTITLE = "Gestionale";
    public static final String LOGO_LOGOUT = "logout";
    public static final String LOGO_BREADCRUMB_EMOJI ="🏠";
    public static final String ICON_SETTINGS ="⚙";
    public static final String ICON_CLOSE ="✕";

    // Navigation Titles
    public static final String NAV_HOME = "Home";
    public static final String NAV_RENTAL = "Catalogo Noleggio";
    public static final String NAV_ADD_ITEM = "Aggiungi Articolo";
    public static final String NAV_LOGIN = "Accesso";
    public static final String NAV_REGISTER = "Registrazione";
    public static final String NAV_PROFILE = "Area Personale";
    public static final String NAV_SETTING = "Impostazioni";
    public static final String NAV_HEADER ="Header";
    public static final String NAV_PRODUCT_CARD ="product card";
    public static final String NAV_RENTAL_CARD ="rental card" ;

    // Chiavi per la homeView
    public static final String LOGO_CARD_RENT_EMOJI ="🔍";
    public static final String LOGO_CARD_ADD_EMOJI ="➕";
    public static final String WELCOME_TITLE = "Benvenuto nel Sistema di Noleggio";
    public static final String WELCOME_DESC = "La piattaforma completa per gestire i tuoi noleggi in modo semplice e veloce. Scegli come vuoi iniziare oggi.";

    public static final String CARD_RENT_TITLE = "Noleggia";
    public static final String CARD_RENT_DESC = "Trova l'attrezzatura perfetta per le tue esigenze nel nostro catalogo.";
    public static final String BTN_RENT_START = "INIZIA NOLEGGIO";
    public static final String ROLE_CLIENT = "RUOLO: CLIENTE";

    public static final String CARD_ADD_TITLE = "Aggiungi Articolo";
    public static final String CARD_ADD_DESC = "Metti a disposizione i tuoi beni e inizia a guadagnare oggi successo.";
    public static final String BTN_ADD_START = "AGGIUNGI ORA";
    public static final String ROLE_OWNER = "RUOLO: PROPRIETARIO";

    // Chiavi per la RegisterView
    public static final String SIGNUP_TITLE = "Registrazione";
    public static final String SIGNUP_SUBTITLE = "Crea il tuo account";
    public static final String SIGNUP_ROLE_LABEL = "Seleziona il tuo ruolo";
    public static final String SIGNUP_EMAIL = "Email";
    public static final String SIGNUP_PASSWORD = "Password";
    public static final String SIGNUP_CONFIRMPASSWORD = "Conferma Password";
    public static final String SIGNUP_SUBMIT = "REGISTRATI";
    public static final String SIGNUP_DEMO = "Demo version - Tutti i campi sono obbligatori";

    public static final String ROLE_CLIENT_NAME = "Cliente";
    public static final String ROLE_CLIENT_DESC = "Noleggia veicoli e attrezzature";
    public static final String ROLE_OWNER_NAME = "Proprietario";
    public static final String ROLE_OWNER_DESC = "Gestisci i tuoi articoli a noleggio";

    // Impostazioni
    public static final String SETTINGS_TITLE = "Impostazioni di Sistema";
    public static final String SETTINGS_THEME_LABEL = "Tema dell'applicazione";
    public static final String ICON_THEME_LIGHT ="☀";
    public static final String ICON_THEME_DARK = "🌙";

    // Catalog & Filter
    public static final String PRODUCT_STATUS_UNAVAILABLE = "Non disponibile";
    public static final String PRODUCT_STATUS_AVAILABLE = "Disponibile subito";
    public static final String CATALOG_EMPTY_MESSAGE = "Nessun articolo disponibile nel catalogo.";
    public static final String FILTER_TITLE = "Cerca nel Catalogo";
    public static final String FILTER_SEARCH_PROMPT = "Cerca per nome o categoria...";
    public static final String FILTER_CATEGORY = "Categoria";
    public static final String FILTER_PRICE = "Prezzo";
    public static final String PRODUCT_CONDITION_LABEL = "Condizione Ottima";
    public static final String PRODUCT_PRICE_FROM = "a partire da";
    public static final String PRODUCT_PRICE_PERDAY = "/giorno";
    public static final String BTN_DETAILS = "DETTAGLI";

    // product Detail
    public static final String PRODUCT_DETAIL_SPECS_TITLE = "Specifiche Tecniche";
    public static final String PRODUCT_DETAIL_PRICE_PERDAY = "EUR / Giorno";
    public static final String PRODUCT_DETAIL_WARRANTY_TITLE = "Garanzia inclusa";
    public static final String PRODUCT_DETAIL_WARRANTY_DESC = "Questo articolo include una copertura assicurativa kasko per danni accidentali durante il periodo di noleggio.";
    public static final String PRODUCT_DETAIL_CONDITIONS_TITLE = "Condizioni di Noleggio";
    public static final String PRODUCT_DETAIL_BOOKING_ESTIMATED = "Prezzo Stimato (7 Giorni)";
    public static final String PRODUCT_DETAIL_BTN_BOOK = "PRENOTA ORA";
    public static final String NAV_PRODUCT_DETAIL = "Dettaglio Prodotto";

    // =========================================================================
    // 🆕 PREFISSI DI PARSING PER LA VIEW (Switch Expression nei GC)
    // =========================================================================
    public static final String PREFIX_TITLE = "TITLE:";
    public static final String PREFIX_SUBTITLE = "SUBTITLE:";
    public static final String PREFIX_DAILY_PRICE = "DAILY_PRICE:";
    public static final String PREFIX_DISCOUNT = "DISCOUNT:";
    public static final String PREFIX_TOTAL_PRICE = "TOTAL_PRICE:";
    public static final String PREFIX_SPECS = "SPECS:";
    public static final String PREFIX_CONDITIONS = "CONDITIONS:";
    public static final String PREFIX_ID = "PRODUCT_ID:";
    public static final String PREFIX_DESCRIPTION = "DESCRIPTION:";

    // Prefissi specifici per RentalFormBean
    public static final String PREFIX_NAME = "NAME:";
    public static final String PREFIX_SURNAME = "SURNAME:";
    public static final String PREFIX_EMAIL = "EMAIL:";
    public static final String PREFIX_PHONE = "PHONE:";
    public static final String PREFIX_PICKUP = "PICKUP:";
    public static final String PREFIX_START_DATE = "START_DATE:";
    public static final String PREFIX_END_DATE = "END_DATE:";
    public static final String PREFIX_STATUS = "STATUS:";

    public static final String TAG_SPECIAL_SUFFIX = " SPECIAL";
    public static final String FALLBACK_SPECS = "Nessuna specifica tecnica dichiarata.";
    public static final String FALLBACK_CONDITIONS = "Condizioni standard di contratto.";

    // Booking Form
    public static final String BOOKING_PERSONAL_TITLE = "Dati dell'Intestatario";
    public static final String BOOKING_PERSONAL_NAME = "Nome";
    public static final String BOOKING_PERSONAL_SURNAME = "Cognome";
    public static final String BOOKING_PERSONAL_EMAIL = "Email";
    public static final String BOOKING_PERSONAL_PHONE = "Telefono";
    public static final String BOOKING_PERSONAL_PICKUP = "Punto di Ritiro";

    public static final String BOOKING_MOCK_EMAIL = "es:mario.rossi@example.com";
    public static final String BOOKING_MOCK_PHONE = "es:+31 415 592 65358";
    public static final String BOOKING_MOCK_PICKUP = "es:roma";

    public static final String BOOKING_PERIOD_TITLE = "Periodo di Noleggio";
    public static final String BOOKING_PERIOD_START = "Data Inizio";
    public static final String BOOKING_PERIOD_END = "Data Fine";

    public static final String BOOKING_SUMMARY_TITLE = "Riepilogo Ordine";
    public static final String BOOKING_SUMMARY_ITEM = "Articolo";
    public static final String BOOKING_SUMMARY_DURATION = "Durata";
    public static final String BOOKING_SUMMARY_SUBTOTAL = "Subtotale";
    public static final String BOOKING_SUMMARY_TOTAL = "TOTALE";
    public static final String BOOKING_BTN_SUBMIT = "CONFERMA PRENOTAZIONE";
    public static final String NAV_BOOKING_FORM = "Modulo di Noleggio";

    public static final String BOOKING_PROMPT_NAME = "Inserisci nome";
    public static final String BOOKING_PROMPT_SURNAME = "Inserisci cognome";

    public static final String BOOKING_SUMMARY_DAILY_PRICE = "Costo Giornaliero";
    public static final String BOOKING_DURATION_SINGLE = " Giorno";
    public static final String BOOKING_DURATION_PLURAL = " Giorni";
    public static final String BOOKING_CURRENCY_FORMAT = "%.2f€";

    public static final String BOOKING_FORM = "form";

    // Success View
    public static final String SUCCESS_TITLE = "Prenotazione Inviata!";
    public static final String SUCCESS_MESSAGE = "La tua richiesta è stata presa in carico correttamente. Riceverai a breve una mail con tutti i dettagli per il ritiro.";
    public static final String SUCCESS_REQUEST_NUMBER = "Numero Richiesta:";
    public static final String SUCCESS_BTN_HOME = "TORNA ALLA HOME";
    public static final String NAV_SUCCESS = "Successo";

    // User Rentals (My Rentals)
    public static final String NAV_USER_RENTAL = "Gestione Noleggi";
    public static final String RENTALS_PRODUCT = "Prodotto:";
    public static final String RENTALS_ID = "ID:";
    public static final String RENTALS_FROM = "Dal:";
    public static final String RENTALS_TO = "Al:";
    public static final String RENTALS_BTN_ACCEPT = "Accetta";
    public static final String RENTALS_BTN_CANCEL = "Cancella";
    public static final String RENTALS_BTN_REJECT = "Rifiuta";
    public static final String RENTALS_BTN_PAY = "paga";
    public static final String RENTALS_BTN_MAGIC = "magic";
    public static final String TEXT_NOT_AVAILABLE = "N/A";
    public static final String STATUS_DEFAULT_PENDING = "PENDING";

    // Messaggi di stato per le liste
    public static final String ERR_INVALID_SESSION = "Sessione non valida o nessun dato disponibile.";
    public static final String RENTALS_EMPTY_MESSAGE = "Nessun noleggio trovato.";

    public static final String ROLE_CLIENT_VAL = "CLIENTE";
    public static final String ROLE_OWNER_VAL = "PROPRIETARIO";

    // =========================================================================
    // ⚠️ STRINGHE DI ERRORE CENTRALIZZATE (Per View e Bean Validation)
    // =========================================================================
    public static final String SIGNUP_ERROR = "Errore Registrazione";
    public static final String LOGIN_ERROR = "Errore Login";
    public static final String SEND_ERROR = "Errore invio";

    // Validation Errors BEAN register
    public static final String ERR_ROLE_INVALID = "Il ruolo selezionato non è valido.";
    public static final String ERR_PASSWORD_MISMATCH = "Le password inserite non corrispondono.";

    // Login Validation BEAN Errors
    public static final String ERR_EMAIL_REQUIRED = "L'email è obbligatoria.";
    public static final String ERR_EMAIL_INVALID = "L'indirizzo email inserito non è valido.";
    public static final String ERR_PASSWORD_REQUIRED = "La password è obbligatoria.";
    public static final String ERR_PASSWORD_SHORT = "La password deve contenere almeno 5 caratteri.";

    // Rental Form Validation Errors
    public static final String ERR_RENTAL_FIELDS_REQUIRED = "Tutti i campi dei dati dell'intestatario sono obbligatori.";
    public static final String ERR_RENTAL_PICKUP_REQUIRED = "Il punto di ritiro è obbligatorio.";
    public static final String ERR_RENTAL_DATES_REQUIRED = "Le date di inizio e fine sono obbligatorie.";
    public static final String ERR_RENTAL_PAST_DATE = "Non è possibile prenotare una data nel passato.";
    public static final String ERR_RENTAL_DATE_ORDER = "La data di fine deve essere successiva alla data di inizio.";
    public static final String ERR_RENTAL_NO_PRODUCT = "Nessun prodotto selezionato per il noleggio.";

    // Symbols & Units
    public static final String CURRENCY_EUR = "€";
    public static final String SUFFIX_PERCENT = "%";

    public static String money(Double value){
        return String.format(java.util.Locale.US, "%.2f", value) + StrApp.CURRENCY_EUR;
    }

    // Product Detail Labels
    public static final String LABEL_DISCOUNT_PREFIX = "Sconto: ";
    public static final String LABEL_WEEKLY_PRICE_PREFIX = "Prezzo Settimanale: ";
    public static final String LABEL_PRODUCT_ID_PREFIX = "ID Prodotto: ";
    public static final String LABEL_DESCRIPTION_PREFIX = "Descrizione: ";
    public static final String LABEL_AVAILABILITY_PREFIX = "Disponibilità: ";
    public static final String LABEL_OWNER_EMAIL_PREFIX = "Email Proprietario: ";
    public static final String LABEL_CATEGORY_PREFIX = "Categoria: ";
    public static final String LABEL_SPECS_PREFIX = "Specifiche: ";
    public static final String LABEL_CONDITIONS_PREFIX = "Condizioni di Noleggio: ";
    public static final String STATUS_AVAILABLE = "Disponibile";
    public static final String STATUS_UNAVAILABLE = "Non disponibile";

    public static final String DOT ="• ";
    public static final String TRUE ="✓ ";

    public static String get(String key) {
        return key;
    }
}