package org.example.model.entity.rental.state;

import org.example.model.entity.rental.StatusEnum;

public class CancelledState implements RentalState {
    @Override
    public void approve(RentalS rental) {
        throw new IllegalStateException("Impossibile approvare: il noleggio è stato annullato.");
    }



    @Override
    public void activate(RentalS rental) {
        throw new IllegalStateException("Impossibile attivare: un noleggio annullato non può iniziare.");
    }

    @Override
    public void complete(RentalS rental) {
        throw new IllegalStateException("Impossibile completare: il noleggio è stato precedentemente annullato.");
    }
    @Override
    public void reject(RentalS rental) {
        throw new IllegalStateException("Impossibile rifiutare: il noleggio risulta già annullato.");
    }

    @Override
    public void cancel(RentalS rental) {
        rental.setInternalState(new CancelledState());
    }

    @Override
    public boolean canBeCancelled() { return true; }

    @Override
    public StatusEnum getStatusEnum() { return StatusEnum.CANCELLED; }
}