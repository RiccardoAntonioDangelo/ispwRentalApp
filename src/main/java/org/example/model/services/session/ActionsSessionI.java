package org.example.model.services.session;

import org.example.model.entity.actors.User;


public interface ActionsSessionI<U extends User> {

    /**
     * Crea una sessione vuota o di default.
     */
    SessionI createSession();

    SessionI createSession(U user);
}
