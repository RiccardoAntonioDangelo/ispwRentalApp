package org.example.controller.application.login;

import org.example.controller.bean.LoginBean;
import org.example.controller.bean.SessionBean;
import org.example.exceptions.AuthenticationException;
import org.example.model.entity.session.Session;

public interface LoginController {

    /**
     * Esegue l'autenticazione e registra la sessione nel manager globale.
     */
    static SessionBean login(String email, String password) throws AuthenticationException {
        Session session = AuthenticationController.authenticate(email, password);

        if (session == null) {throw new AuthenticationException("Credenziali non valide o utente inesistente.");}
        if (SessionManager.registerSession(session)) {return new SessionBean(session);}

        throw new AuthenticationException("L'utente con email " + email + " ha già una sessione attiva.");
    }

    /**
     * Login tramite Bean (punto di ingresso per la Boundary/Vista)
     */
    static SessionBean login(LoginBean bean) throws AuthenticationException {
        if (!bean.isValid()) {throw new AuthenticationException(bean.getErrorMessage());}
        return login(bean.getEmail(), bean.getPassword());
    }

    /**
     * Esegue il logout rimuovendo la sessione dal registro globale.
     */
    static void logout(SessionBean sessionBean) {if (sessionBean != null) {SessionManager.closeSession(sessionBean.getSession());sessionBean.logout();}}

    /**
     * Verifica se la sessione è ancora valida nel contesto del server.
     */
    static boolean isAuthenticated(SessionBean sessionBean) {
        if (sessionBean == null) return false;
        return SessionManager.isUserLoggedIn(sessionBean.getSession().getId());
    }
}