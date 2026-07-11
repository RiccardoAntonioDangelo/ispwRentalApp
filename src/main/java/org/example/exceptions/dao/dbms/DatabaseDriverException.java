package org.example.exceptions.dao.dbms;

/**
 * Eccezione personalizzata per errori relativi ai Driver JDBC.
 * Estendiamo RuntimeException così non siamo obbligati a gestire
 * l'eccezione con un try-catch ogni volta che usiamo la Factory.
 */
public class DatabaseDriverException extends RuntimeException {

    // Costruttore che accetta solo il messaggio
    public DatabaseDriverException(String message) {
        super(message);
    }

    // Costruttore che accetta messaggio e la causa originale (es. ClassNotFoundException)
    public DatabaseDriverException(String message, Throwable cause) {
        super(message, cause);
    }
}