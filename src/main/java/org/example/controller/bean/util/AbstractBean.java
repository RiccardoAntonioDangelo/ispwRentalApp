package org.example.controller.bean.util;


/**
 * Base astratta per tutti i Bean che richiedono validazione dell'input
 * e gestione dei messaggi di errore per la View.
 */
public abstract class AbstractBean {

    // Campo protetto per permettere alle sottoclassi di impostare l'errore
    private String errorMessage = "";

    /**
     * Esegue la validazione del Bean. 
     * Ogni Bean figlio implementerà qui la propria logica (es. controllo email, password, date).
     * * @return true se i dati sono validi, false altrimenti.
     */
    public abstract boolean isValid();
    public boolean demoValid(){return true;}

    /**
     * Restituisce il messaggio di errore corrente e lo "consuma" (lo resetta).
     * Questo evita che il vecchio errore rimanga visibile nei tentativi successivi.
     * * @return Il messaggio di errore o una stringa vuota se non ci sono errori.
     */
    public String getErrorMessage() {
        String currentMessage = this.errorMessage;
        this.errorMessage = ""; // Reset dell'errore dopo la lettura
        return currentMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Verifica se è presente un errore senza resettarlo.
     */
    public boolean hasError() {
        return this.errorMessage != null && !this.errorMessage.trim().isEmpty();
    }
}