package org.example.model.services.rent;

import org.example.exceptions.dao.file.EntityNotFoundException;
import org.example.model.entity.rental.StatusEnum;
import org.example.model.services.CollectionI;
import org.example.model.services.session.SessionI;
import java.time.LocalDate;

public interface ActionsRentI {
    
    /**
     * Inizializza la collezione specifica in base alla chiave fornita.
     */
    static void initRentCollection(SessionI session, String key) {
        if (session == null) throw new IllegalArgumentException("Sessione null");
        session.ensureCollection(key);
    }

    /**
     * Aggiunge il noleggio alla sessione usando la chiave specifica dell'interfaccia chiamante.
     */
    static void addRentToSession(SessionI session, RentI rent, String key) {
        try {
            session.addItem(key, rent);
        }catch (Exception e){
            initRentCollection(session, key);
            session.addItem(key, rent);
        }
    }

    /**
     * Recupera la collezione specifica dalla sessione usando la chiave.
     */
    static CollectionI<RentI> getRentsFromSession(SessionI session, String key) {
        if (session == null) throw new IllegalArgumentException("Sessione null");
        try {
            CollectionI<RentI> collection = session.getCollection(key);
            if (collection == null) throw new EntityNotFoundException("collection null");
            return collection;
        }catch (Exception e){
            session.ensureCollection(key);
            return session.getCollection(key);
        }
    }

    /**
     * Logica di polling pigro condivisa per la scadenza.
     */
    static boolean completeIfExpired(RentI rent) {
        if (rent == null) return false;
        if (rent.getStatus() == StatusEnum.ACTIVE
                && rent.getEndDate() != null
                && LocalDate.now().isAfter(rent.getEndDate())) {
            rent.complete();
            return true;
        }
        return false;
    }
    static void removeRentToSession(SessionI session, RentI rent, String key) {
        try {
            session.removeItem(key, rent);
        }catch (Exception e){
            initRentCollection(session, key);
        }
    }

    /**
     * Metodo di interazione: connette la sessione del client e dell'owner salvando 
     * il noleggio nelle rispettive e distinte collezioni.
     */
    static void start(SessionI clientSession, SessionI ownerSession, RentI rent) {
        if (clientSession == null || rent == null || ownerSession==null ) throw new IllegalArgumentException("IllegalArgument null");
        if(clientSession.getRole() instanceof ActionsClientRentI clientRentI && ownerSession.getRole() instanceof ActionsOwnerRentI ownerRentI){
            clientRentI.requestRent(clientSession,rent);
            //if(!ownerRentI.myRent(rent))throw new IllegalArgumentException("owner non e' il proprietario ");
            ownerRentI.receiveRent(ownerSession,rent);
        }
    }
    static boolean cancelRent(SessionI session,RentI rent) {
        boolean value=false;
        if(session.getRole() instanceof ActionsClientRentI clientRentI)
            value= clientRentI.cancelClientRent(session,rent);
        if(session.getRole() instanceof ActionsOwnerRentI ownerRentI)//todo verifica se sei il vero proprietario
           value= ownerRentI.cancelOwnerRent(session,rent) || value ;
        return value;
    }
}