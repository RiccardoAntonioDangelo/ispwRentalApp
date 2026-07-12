package org.example.model.entity.rental.state;

import org.example.model.entity.rental.AbstractRental;
import org.example.model.entity.rental.StatusEnum;
import java.time.LocalDate;

public class RentalS extends AbstractRental<RentalS> implements RentalContext {

    // Il pattern State sostituisce il semplice Enum
    private RentalState state = new PendingState();

    public RentalS() {
        super();
    }

    public RentalS(String clientEmail, String clientPhone, String productId, String locate, LocalDate start, LocalDate end) {
        super(clientEmail, clientPhone, productId, locate, start, end);
    }

    public RentalS(String clientEmail, String ownerEmail, String productId) {
        super(clientEmail, ownerEmail, productId);
    }

    public void setInternalState(RentalState state) {
        this.state = state;
        this.commitChange();
    }

    // =========================================================================
    // DELEGA DELLE AZIONI ALLO STATO
    // =========================================================================

    @Override public void approve() { this.state.approve(this); }
    @Override public void reject() { this.state.reject(this); }
    @Override public void activate() { this.state.activate(this); }
    @Override public void complete() { this.state.complete(this); }
    @Override public void cancel() { this.state.cancel(this); }
    @Override public boolean canBeCancelled() { return this.state.canBeCancelled(); }

    @Override
    public StatusEnum getStatus() {
        return this.state.getStatusEnum();
    }

    @Override
    public RentalS setStatus(StatusEnum status) {
        if (status == null) {
            this.state = new PendingState();
            return this;
        }
        this.state = switch (status) {
            case PENDING -> new PendingState();
            case APPROVED -> new ApprovedState();
            case ACTIVE -> new ActiveState();
            case COMPLETED -> new CompletedState();
            case CANCELLED -> new CancelledState();
            case REJECTED -> new RejectedState();
        };
        this.commitChange();
        return this;
    }
}