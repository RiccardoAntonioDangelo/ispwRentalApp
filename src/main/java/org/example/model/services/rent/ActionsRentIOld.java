package org.example.model.services.rent;


import org.example.model.entity.rental.StatusEnum;
import org.example.model.services.CollectionI;
import org.example.model.services.session.SessionI;

import java.time.LocalDate;


/**
 * interfaccia per il comportamento di noleggio.
 * Gestisce l'associazione tra un utente (tramite sessione) e un articolo.
 */
public interface ActionsRentIOld {
    String RENTAL_COLLECTION_KEY = ActionsRentIOld.class.getSimpleName();
    private void initRentI(SessionI sessionI){//todo poso pure tentare e poi mettere un try
        sessionI.ensureCollection(RENTAL_COLLECTION_KEY);
    }
    private void addRent(RentI rent, SessionI sessionI){
        initRentI(sessionI);
        sessionI.addItem(RENTAL_COLLECTION_KEY,rent);
    }
    default void rmRent(RentI rent, SessionI sessionI){//todo nome piu di dominio
        initRentI(sessionI);
        sessionI.removeItem(RENTAL_COLLECTION_KEY,rent);
    }
    default CollectionI<RentI> getRents(SessionI sessionI){
        return sessionI.getCollection(RENTAL_COLLECTION_KEY);
    }
    default boolean execute(SessionI session, RentI rent) {
        if (session == null) {throw new IllegalArgumentException("Sessione non valida durante l'esecuzione del noleggio.");}
        addRent(rent,session);
        return true;
    }

    default boolean completeIfExpired(RentI rent) {
        if (rent == null) return false;
        if (rent.getStatus() == StatusEnum.ACTIVE
                && rent.getEndDate() != null
                && LocalDate.now().isAfter(rent.getEndDate())) {
            rent.complete();
            return true;
        }
        return false;
    }

    /**
     * Esegue il noleggio di un articolo.
     * * @param currentSession La sessione attiva dell'utente che noleggia.
     * @param item           L'articolo da noleggiare (es. un'Entity Prodotto).
     * @param startDate      Data di inizio noleggio.
     * @param endDate        Data di fine noleggio.
     * @return true se il noleggio è andato a buon fine, false altrimenti.
     */
    //boolean rent(SessionI currentSession, T item, LocalDate startDate, LocalDate endDate);

    /**
     * Metodo di default per calcolare un costo preventivo (comportamento logico).
     */
    default double calculateEstimate(double pricePerDay, long days) {
        return pricePerDay * days;
    }
}
