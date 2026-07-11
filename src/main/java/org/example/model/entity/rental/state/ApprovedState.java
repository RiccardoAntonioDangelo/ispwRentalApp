package org.example.model.entity.rental.state;

import org.example.model.entity.rental.StatusEnum;

public class ApprovedState implements RentalState {
    @Override
    public void approve(RentalS rental) {
        throw new IllegalStateException("Il noleggio è già stato approvato.");
    }

    @Override
    public void reject(RentalS rental) {
        throw new IllegalStateException("Impossibile rifiutare: il noleggio è già stato approvato (usa 'cancel' se consentito).");
    }

    @Override
    public void activate(RentalS rental) {
        // Transizione valida: il noleggio approvato viene ritirato/avviato
        rental.setInternalState(new ActiveState());
    }

    @Override
    public void complete(RentalS rental) {
        throw new IllegalStateException("Impossibile completare: il noleggio deve prima essere attivato.");
    }

    @Override
    public void cancel(RentalS rental) {
        // Transizione valida: il cliente annulla un noleggio approvato prima dell'inizio
        rental.setInternalState(new CancelledState());
    }

    @Override
    public boolean canBeCancelled() { return true; }

    @Override
    public StatusEnum getStatusEnum() { return StatusEnum.APPROVED; }
}