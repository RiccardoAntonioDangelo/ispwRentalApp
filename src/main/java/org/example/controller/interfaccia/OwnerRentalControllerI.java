package org.example.controller.interfaccia;

import org.example.controller.bean.RentalFormBean;
import org.example.controller.bean.SessionBean;
import org.example.exceptions.RentalException;

public interface OwnerRentalControllerI extends ClientRentalControllerI {

    // FASE 4: Accettare la richiesta di noleggio del cliente
    void acceptRental(SessionBean session, RentalFormBean rentalFormBean) throws RentalException;
}