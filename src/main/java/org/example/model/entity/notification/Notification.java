package org.example.model.entity.notification;

import org.example.model.services.notification.NotificationI;
import org.example.util.str.StrApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Notification implements NotificationI {

    private String id;
    private String message;
    private String recipientEmail;
    private String senderEmail;
    private boolean read = false;

    public Notification() {}

    /**
     * Costruttore completo per inizializzare al volo una nuova notifica.
     */
    public Notification(String senderEmail, String recipientEmail, String message) {
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.id = senderEmail + "I" + recipientEmail + "I" + System.currentTimeMillis();
    }

    public void commitChange() {
        this.notifyObservers(this);
    }

    // =========================================================================
    // METODI PER ARGOMENTI IN STRINGA (Sincronizzati con la UI)
    // =========================================================================

    public List<String> getReducedDetails() {
        List<String> details = new ArrayList<>();
        details.add(StrApp.PREFIX_TITLE + "Notifica");
        details.add(StrApp.PREFIX_DESCRIPTION + this.message);
        return details;
    }

    public List<String> getCompleteDetails() {
        List<String> details = new ArrayList<>(getReducedDetails());
        details.add(StrApp.PREFIX_ID + getId());
        details.add(StrApp.PREFIX_EMAIL + this.senderEmail);
        details.add(StrApp.LABEL_OWNER_EMAIL_PREFIX + this.recipientEmail);
        details.add(StrApp.PREFIX_STATUS + (this.read ? "LETTA" : "NUOVA"));
        return details;
    }

    // =========================================================================
    // GETTERS & SETTERS IN METHOD CHAINING (Fluent API) con Auto-Persistenza
    // =========================================================================

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Notification setMessage(String message) {
        if (!Objects.equals(this.message, message)) {
            this.message = message;
            this.commitChange();
        }
        return this;
    }

    @Override
    public String getRecipientEmail() {
        return recipientEmail;
    }

    @Override
    public Notification setRecipientEmail(String recipientEmail) {
        if (!Objects.equals(this.recipientEmail, recipientEmail)) {
            this.recipientEmail = recipientEmail;
            this.commitChange();
        }
        return this;
    }

    @Override
    public String getSenderEmail() {
        return senderEmail;
    }

    @Override
    public Notification setSenderEmail(String senderEmail) {
        if (!Objects.equals(this.senderEmail, senderEmail)) {
            this.senderEmail = senderEmail;
            this.commitChange();
        }
        return this;
    }

    @Override
    public boolean isRead() {
        return read;
    }

    @Override
    public Notification setRead(boolean read) {
        if (this.read != read) {
            this.read = read;
            this.commitChange();
        }
        return this;
    }

    public Notification setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getId() {
        return this.id;
    }
}