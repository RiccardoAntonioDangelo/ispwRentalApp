package org.example.model.entity.rental.state;

import org.example.model.entity.rental.StatusEnum;

public class CompletedState implements RentalState {
    @Override
    public void approve(RentalS rental) {
        throw new IllegalStateException("Impossibile approvare: il noleggio è già terminato e completato.");
    }

    @Override
    public void reject(RentalS rental) {
        throw new IllegalStateException("Impossibile rifiutare: il noleggio si è già concluso.");
    }

    @Override
    public void activate(RentalS rental) {
        throw new IllegalStateException("Impossibile attivare: il noleggio è storico e già completato.");
    }

    @Override
    public void complete(RentalS rental) {
        throw new IllegalStateException("Il noleggio è già stato completato.");
    }

    @Override
    public void cancel(RentalS rental) {
        rental.setInternalState(new CancelledState());
    }

    @Override
    public boolean canBeCancelled() { return true; }

    @Override
    public StatusEnum getStatusEnum() { return StatusEnum.COMPLETED; }
}