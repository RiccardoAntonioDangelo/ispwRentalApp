package org.example.model.services.rent;

import org.example.model.services.CollectionI;
import org.example.model.services.session.SessionI;

public interface ActionsOwnerRentI {
    // Stringa specifica per distinguere dove l'owner riceve i noleggi
    String RENTAL_COLLECTION_KEY = ActionsOwnerRentI.class.getSimpleName();

    private String getKey() {
        return RENTAL_COLLECTION_KEY;
    }

    default void receiveRent(SessionI session, RentI rent) {
        if (session == null) throw new IllegalArgumentException("Sessione non valida.");
        if (rent == null)throw new IllegalArgumentException("rent non valida.");

        // Chiama il metodo statico passando la chiave dell'owner
        ActionsRentI.addRentToSession(session, rent, getKey());
    }

    default void acceptRent(RentI rent) {
        if (rent == null)throw new IllegalArgumentException("rent non valida.");
        rent.approve();
    }

    default void rejectRent(RentI rent) {
        if (rent == null)throw new IllegalArgumentException("rent non valida.");
        rent.reject();
    }

    default void activateRent(RentI rent) {
        if (rent == null)throw new IllegalArgumentException("rent non valida.");
        rent.activate();
    }

    default void completeRent(RentI rent) {
        if (rent == null)throw new IllegalArgumentException("rent non valida.");
        rent.complete();
    }



    default CollectionI<RentI> getOwnerRents(SessionI session) {
        return ActionsRentI.getRentsFromSession(session, getKey());
    }

    default boolean cancelOwnerRent(SessionI session,RentI rent) {
        if (rent == null)throw new IllegalArgumentException("rent non valida.");
        if ( !rent.canBeCancelled()) return false;
        rent.cancel();
        ActionsRentI.removeRentToSession(session,rent,getKey());
        return true;
    }
}