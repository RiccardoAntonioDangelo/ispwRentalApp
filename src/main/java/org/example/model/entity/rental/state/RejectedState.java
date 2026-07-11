package org.example.model.entity.rental.state;

import org.example.model.entity.rental.StatusEnum;

public class RejectedState implements RentalState {
    @Override
    public void approve(RentalS rental) {
        throw new IllegalStateException("Impossibile approvare: la richiesta è già stata rifiutata dal proprietario.");
    }

    @Override
    public void reject(RentalS rental) {
        throw new IllegalStateException("La richiesta di noleggio è già stata rifiutata.");
    }

    @Override
    public void activate(RentalS rental) {
        throw new IllegalStateException("Impossibile attivare: la richiesta di noleggio è stata rifiutata.");
    }

    @Override
    public void complete(RentalS rental) {
        throw new IllegalStateException("Impossibile completare: il noleggio non è mai stato accettato.");
    }

    @Override
    public void cancel(RentalS rental) {
        rental.setInternalState(new CancelledState());
    }

    @Override
    public boolean canBeCancelled() { return true; }

    @Override
    public StatusEnum getStatusEnum() { return StatusEnum.REJECTED; }
}