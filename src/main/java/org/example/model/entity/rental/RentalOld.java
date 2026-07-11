package org.example.model.entity.rental;

import org.example.model.entity.actors.User;
import org.example.model.services.rent.ActionsOwnerRentI;
import org.example.model.services.rent.RentI;

import java.time.LocalDate;
import java.util.Objects;

public class RentalOld implements RentI {

    private String clientEmail;
    private String clientPhone;
    private String ownerEmail;
    private String productId;
    private String locate;
    private LocalDate startDate;
    private LocalDate endDate;
    private StatusEnum status = StatusEnum.PENDING;
    private Payment payment;

    public RentalOld() {}

    public RentalOld(String clientEmail, String clientPhone, String productId, String locate, LocalDate start, LocalDate end) {
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.productId = productId;
        this.startDate = start;
        this.endDate = end;
        this.locate = locate;
    }

    public RentalOld(String clientEmail, String ownerEmail, String productId) {
        this.clientEmail = clientEmail;
        this.ownerEmail = ownerEmail;
        this.productId = productId;
        this.startDate = LocalDate.now();
    }

    public void commitChange() {
        this.notifyObservers(this);
    }

    // =========================================================================
    // IMPLEMENTAZIONE DEI METODI DI TRANSIZIONE (Logica centralizzata sull'Enum)
    // =========================================================================

    @Override
    public void approve() {
        if (this.status != StatusEnum.PENDING) {
            throw new IllegalStateException("Impossibile approvare: il noleggio è in stato " + this.status);
        }
        this.status = StatusEnum.APPROVED;
        this.commitChange();
    }

    @Override
    public void reject() {
        if (this.status != StatusEnum.PENDING) {
            throw new IllegalStateException("Impossibile rifiutare: il noleggio è in stato " + this.status);
        }
        this.status = StatusEnum.REJECTED;
        this.commitChange();
    }

    @Override
    public void activate() {
        if (this.status != StatusEnum.APPROVED) {
            throw new IllegalStateException("Impossibile attivare/pagare: il noleggio deve prima essere APPROVED (stato attuale: " + this.status + ")");
        }
        this.status = StatusEnum.ACTIVE;
        this.commitChange();
    }

    @Override
    public void complete() {
        if (this.status != StatusEnum.ACTIVE) {
            throw new IllegalStateException("Impossibile completare: il noleggio deve essere ACTIVE (stato attuale: " + this.status + ")");
        }
        this.status = StatusEnum.COMPLETED;
        this.commitChange();
    }

    @Override
    public void cancel() {
        if (!this.canBeCancelled()) {
            throw new IllegalStateException("Impossibile annullare il noleggio nello stato attuale: " + this.status);
        }
        this.status = StatusEnum.CANCELLED;
        this.commitChange();
    }

    @Override
    public boolean canBeCancelled() {
        // Riutilizza la logica delegata all'enum o centralizzata
        return this.status == StatusEnum.PENDING || this.status == StatusEnum.APPROVED;
    }

    @Override
    public void setPayment(Payment payment) {
        this.payment = payment;
        this.commitChange();
    }

    // =========================================================================
    // GETTERS & SETTERS IN METHOD CHAINING (Fluent API)
    // =========================================================================

    public String getClientEmail() {
        return clientEmail;
    }

    public RentalOld setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
        this.commitChange();
        return this;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public RentalOld setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
        this.commitChange();
        return this;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public RentalOld setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        this.commitChange();
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public RentalOld setProductId(String productId) {
        this.productId = productId;
        this.commitChange();
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public RentalOld setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.commitChange();
        return this;
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

    public RentalOld setLocate(String locate) {
        this.locate = locate;
        this.commitChange();
        return this;
    }

    public StatusEnum getStatus() {
        return status;
    }

    @Override
    public boolean isOwner(ActionsOwnerRentI actionsOwnerRentI) {//todo
        if(actionsOwnerRentI instanceof User user){
        return Objects.equals(this.getOwnerEmail(), user.getEmail());}
        throw new IllegalArgumentException("isOwner SOLO se user e valutabile");
    }

    public RentalOld setStatus(StatusEnum status) {
        this.status = status;
        this.commitChange();
        return this;
    }

    public Payment getPayment() {
        return payment;
    }

    // =========================================================================
    // UTILITY DI GRUPPO
    // =========================================================================
    public RentalOld updateFields(String clientEmail, String clientPhone, String productId, String locate, LocalDate startDate, LocalDate endDate) {
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.productId = productId;
        this.locate = locate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.commitChange();
        return this;
    }

    @Override
    public String getId() {
        return clientEmail + "I" + productId;
    }
}