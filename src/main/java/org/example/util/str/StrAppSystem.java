package org.example.util.str;

/**
 * Classe per la gestione centralizzata delle stringhe (I18n simulato).
 */
public class StrAppSystem {private StrAppSystem() {}

    public static String get(String key) {
        return key; // In una implementazione reale, qui cercheresti in un bundle
    }
    // Stringhe di Errore per il Caricamento delle Viste (ViewLoader)
    public static final String ERR_FXML_NOT_FOUND = "File FXML non trovato nel classpath tramite la stringa fornita: ";
    public static final String ERR_CONTROLLER_NULL = "Il javafx non può essere null.";
    public static final String ERR_VIEW_ALREADY_INIT = "La vista per questo javafx è già stata inizializzata.";
    public static final String ERR_FXML_LOAD_FAILED = "Impossibile caricare l'FXML dall'URL: ";
    public static final String ERR_PATH_EMPTY = "Il percorso specificato non può essere null o vuoto.";
    // Stringhe di Errore per il Caricamento delle Viste (FactoryGC)
    public static final String ERR_FACTORY_GC = "non ancora implementato.";
    // Messaggi derivati dagli Internal Steps e Use Case
    public static final String MSG_CONFIRM_ACCEPTED = "noleggio accettato";
    public static final String MSG_CONFIRM_REJECTED = "noleggio rifiutato";
    public static final String ERR_AUTH_FAILED = "Credenziali errate";
    public static final String ERR_ITEM_UNAVAILABLE = "articolo non disponibile";
}
