package org.example.model.entity.rental.state;

import org.example.model.entity.rental.StatusEnum;

public class ActiveState implements RentalState {
    @Override
    public void approve(RentalS rental) {
        throw new IllegalStateException("Azione non valida: il noleggio è già attivo ed è già stato approvato.");
    }

    @Override
    public void reject(RentalS rental) {
        throw new IllegalStateException("Azione non valida: il noleggio è già in corso, non può essere rifiutato.");
    }

    @Override
    public void activate(RentalS rental) {
        throw new IllegalStateException("Il noleggio è già attualmente attivo.");
    }

    @Override
    public void complete(RentalS rental) {
        // Transizione valida: il periodo di noleggio termina con successo
        rental.setInternalState(new CompletedState());
    }

    @Override
    public void cancel(RentalS rental) {
        throw new IllegalStateException("Non puoi annullare un noleggio già attivo ed in corso.");
    }

    @Override
    public boolean canBeCancelled() { return false; }

    @Override
    public StatusEnum getStatusEnum() { return StatusEnum.ACTIVE; }
}