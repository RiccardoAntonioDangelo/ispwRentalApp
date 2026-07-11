package org.example.model.services.rent;

import org.example.model.entity.rental.Payment;
import org.example.model.services.CollectionI;
import org.example.model.services.session.SessionI;

public interface ActionsClientRentI {
    // Stringa specifica per distinguere dove il client salva i suoi noleggi
    String RENTAL_COLLECTION_KEY =ActionsClientRentI.class.getSimpleName();

    private String getKey() {
        return RENTAL_COLLECTION_KEY;
    }

    default boolean requestRent(SessionI session, RentI rent) {
        if (session == null) throw new IllegalArgumentException("Sessione non valida.");
        if (rent == null)throw new IllegalArgumentException("rent non valida.");

        // Chiama il metodo statico passando la propria chiave
        ActionsRentI.addRentToSession(session, rent, getKey());
        return true;
    }

    default boolean payRent(RentI rent, Payment payment) {
        if (payment == null) throw new IllegalArgumentException("payment non valida.");
        if (rent == null)throw new IllegalArgumentException("rent non valida.");
        rent.setPayment(payment);
        rent.activate();
        return true;
    }

    default boolean cancelClientRent(SessionI session, RentI rent) {
        if (rent == null)throw new IllegalArgumentException("rent non valida.");
        if ( !rent.canBeCancelled()) return false;
        rent.cancel();
        ActionsRentI.removeRentToSession(session,rent,getKey());
        return true;
    }

    default CollectionI<RentI> getClientRents(SessionI session) {
        return ActionsRentI.getRentsFromSession(session, getKey());
    }
}