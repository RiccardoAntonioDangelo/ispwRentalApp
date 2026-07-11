package org.example.exceptions.dao.file;

// Eccezione per errori tecnici di persistenza (I/O, JSON corrotto)
public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) { super(message); }
    public PersistenceException(String message, Throwable cause) { super(message, cause); }
}
