package org.example.model.entity.rental.state;

import org.example.model.entity.rental.StatusEnum;

public class PendingState implements RentalState {
    @Override
    public void approve(RentalS rental) {
        // Transizione valida: il noleggio in attesa viene approvato
        rental.setInternalState(new ApprovedState());
    }

    @Override
    public void reject(RentalS rental) {
        // Transizione valida: il noleggio in attesa viene rifiutato
        rental.setInternalState(new RejectedState());
    }

    @Override
    public void complete(RentalS rental) {
        throw new IllegalStateException("Impossibile completare: il noleggio è ancora in attesa di approvazione.");
    }

    @Override
    public void cancel(RentalS rental) {
        // Transizione valida: il cliente annulla la richiesta prima dell'approvazione
        rental.setInternalState(new CancelledState());
    }

    @Override
    public void activate(RentalS rental) {
        throw new IllegalStateException("Impossibile attivare: il noleggio deve prima essere approvato.");
    }

    @Override
    public boolean canBeCancelled() { return true; }

    @Override
    public StatusEnum getStatusEnum() { return StatusEnum.PENDING; }
}