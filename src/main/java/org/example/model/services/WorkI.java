package org.example.model.services;

import org.example.model.services.session.SessionI;
import org.example.model.services.user.UserI;

public interface WorkI {
    // Questo metodo controllerà se l'utente ha le capacità necessarie ed eseguirà l'azione
    boolean canWork(SessionI sessionI, UserI userI);
}