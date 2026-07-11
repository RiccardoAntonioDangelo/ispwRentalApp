package org.example.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String role) {
        super("Errore: il ruolo '" + role + "' non è stato trovato nel sistema.");
    }
}