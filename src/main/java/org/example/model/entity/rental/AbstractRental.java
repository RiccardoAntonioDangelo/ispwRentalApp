package org.example.model.entity.rental;

import org.example.model.entity.actors.User;
import org.example.model.services.rent.ActionsOwnerRentI;
import org.example.model.services.rent.RentI;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Classe astratta che comprime lo stato dati comune e i comportamenti condivisi.
 * Usa i Generics <T> per permettere il corretto funzionamento della Fluent API (Method Chaining).
 */
public abstract class AbstractRental<T extends AbstractRental<T>> implements RentI {

    protected String clientEmail;
    protected String clientPhone;
    protected String ownerEmail;
    protected String productId;
    protected String locate;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected Payment payment;

    protected AbstractRental() {}

    protected AbstractRental(String clientEmail, String clientPhone, String productId, String locate, LocalDate start, LocalDate end) {
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.productId = productId;
        this.startDate = start;
        this.endDate = end;
        this.locate = locate;
    }

    protected AbstractRental(String clientEmail, String ownerEmail, String productId) {
        this.clientEmail = clientEmail;
        this.ownerEmail = ownerEmail;
        this.productId = productId;
        this.startDate = LocalDate.now(ZoneId.systemDefault());
    }

    public void commitChange() {
        this.notifyObservers(this);
    }

    // Metodo helper per fare il cast sicuro al tipo della sottoclasse nei setter fluent
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    // =========================================================================
    // GETTERS & SETTERS IN METHOD CHAINING (Fluent API)
    // =========================================================================

    public String getClientEmail() {
        return clientEmail;
    }

    public T setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
        this.commitChange();
        return self();
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public T setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
        this.commitChange();
        return self();
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public T setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        this.commitChange();
        return self();
    }

    public String getProductId() {
        return productId;
    }

    public T setProductId(String productId) {
        this.productId = productId;
        this.commitChange();
        return self();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public T setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.commitChange();
        return self();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.commitChange();
    }

    public String getLocate() {
        return locate;
    }

    public T setLocate(String locate) {
        this.locate = locate;
        this.commitChange();
        return self();
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public void setPayment(Payment payment) {
        this.payment = payment;
        this.commitChange();
    }

    // =========================================================================
    // UTILITY & IDENTIFICATIVI
    // =========================================================================

    @Override
    public boolean isOwner(ActionsOwnerRentI actionsOwnerRentI) {
        if (actionsOwnerRentI instanceof User user) {
            return Objects.equals(this.getOwnerEmail(), user.getEmail());
        }
        throw new IllegalArgumentException("isOwner SOLO se user è valutabile");
    }

    public T updateFields(String clientEmail, String clientPhone, String productId, String locate, LocalDate startDate, LocalDate endDate) {
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.productId = productId;
        this.locate = locate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.commitChange();
        return self();
    }

    @Override
    public String getId() {
        return clientEmail + "I" + productId;
    }

    // Firma astratta per costringere le sottoclassi a implementare la gestione del formato enum
    public abstract T setStatus(StatusEnum status);
}