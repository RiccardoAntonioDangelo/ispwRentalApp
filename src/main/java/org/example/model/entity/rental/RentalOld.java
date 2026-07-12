package org.example.model.entity.rental;

import java.time.LocalDate;

public class RentalOld extends AbstractRental<RentalOld> {

    private StatusEnum status = StatusEnum.PENDING;

    public RentalOld() {
        super();
    }

    public RentalOld(String clientEmail, String clientPhone, String productId, String locate, LocalDate start, LocalDate end) {
        super(clientEmail, clientPhone, productId, locate, start, end);
    }

    public RentalOld(String clientEmail, String ownerEmail, String productId) {
        super(clientEmail, ownerEmail, productId);
    }

    // =========================================================================
    // TRANSIZIONI DI STATO SU ENUM
    // =========================================================================

    @Override
    public void approve() {
        if (this.status != StatusEnum.PENDING) {
            throw new IllegalStateException("Impossibile approvare: stato " + this.status);
        }
        this.status = StatusEnum.APPROVED;
        this.commitChange();
    }

    @Override
    public void reject() {
        if (this.status != StatusEnum.PENDING) {
            throw new IllegalStateException("Impossibile rifiutare: stato " + this.status);
        }
        this.status = StatusEnum.REJECTED;
        this.commitChange();
    }

    @Override
    public void activate() {
        if (this.status != StatusEnum.APPROVED) {
            throw new IllegalStateException("Impossibile attivare: stato " + this.status);
        }
        this.status = StatusEnum.ACTIVE;
        this.commitChange();
    }

    @Override
    public void complete() {
        if (this.status != StatusEnum.ACTIVE) {
            throw new IllegalStateException("Impossibile completare: stato " + this.status);
        }
        this.status = StatusEnum.COMPLETED;
        this.commitChange();
    }

    @Override
    public void cancel() {
        if (!this.canBeCancelled()) {
            throw new IllegalStateException("Impossibile annullare in stato: " + this.status);
        }
        this.status = StatusEnum.CANCELLED;
        this.commitChange();
    }

    @Override
    public boolean canBeCancelled() {
        return this.status == StatusEnum.PENDING || this.status == StatusEnum.APPROVED;
    }

    @Override
    public StatusEnum getStatus() {
        return status;
    }

    @Override
    public RentalOld setStatus(StatusEnum status) {
        this.status = status;
        this.commitChange();
        return this;
    }
}