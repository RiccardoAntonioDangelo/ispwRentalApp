package org.example.view;

import org.example.controller.application.RentalController2;
import org.example.controller.bean.*;
import org.example.controller.application.login.LoginController;
import org.example.controller.application.login.RegisterController;
import org.example.exceptions.AuthenticationException;
import org.example.exceptions.RegistrationException;
import org.example.exceptions.RentalException;
import org.example.model.entity.actors.factory.ActorEnum;
import org.example.util.str.StrAppSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade grafica (GraphicAPI) centralizzata che fa da ponte tra l'interfaccia utente JavaFX
 * e i controller logici di business (Application Controllers/Managers).
 * Integra le regole definite negli Internal Steps dello Use Case "Noleggia articolo".
 */
public class GraphicAPI {

    // Costruttore privato per prevenire l'istanziazione di una classe utility pura
    private GraphicAPI() {}

    /**
     * Ritorna la lista di vettori grafici filtrati automaticamente dal flag dell'enum dei ruoli.
     */
    public static List<String[]> getAllRoles() {
        List<String[]> roles = new ArrayList<>();
        for (ActorEnum actor : ActorEnum.values()) {
            if (actor.isGraphicUsable()) roles.add(actor.toGraphicVector());
        }
        return roles;
    }

    /**
     * Gestisce la registrazione di un nuovo account nel sistema.
     */
    public static SessionBean registerApi(RegisterBean registerData) throws RegistrationException {
        return RegisterController.register(registerData);
    }

    /**
     * Effettua il login del cliente.
     * [Internal Step 3a]: Se le credenziali falliscono, AuthenticationException propaga
     * il testo associato a StrAppSystem.ERR_AUTH_FAILED ("Credenziali errate").
     */
    public static SessionBean loginApi(LoginBean loginData) throws AuthenticationException {
        try {
            return LoginController.login(loginData);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(StrAppSystem.get(StrAppSystem.ERR_AUTH_FAILED)+":"+e);
        }
    }

    /**
     * Esegue la disconnessione (logout) dell'utente invalidando la sessione corrente.
     */
    public static void logoutApi(SessionBean sessionBean) {
        LoginController.logout(sessionBean);
    }

    /**
     * Recupera il catalogo complessivo degli annunci.
     * Utilizzato dal sistema per mostrare gli articoli disponibili a seguito del caricamento dati.
     */
    public static CatalogBean getCatalog() {
        return RentalController2.getCatalog();
    }

    /**
     * Genera e pre-popola il MODULO di noleggio (Storyboard 2.5) abbinando la sessione cliente al prodotto scelto.
     * Verifica preventivamente la reale disponibilità dell'annuncio.
     * * [Internal Step 10a]: Se l'articolo è rimosso o già occupato, solleva un'eccezione bloccante.
     */
//    public static RentalFormBean getRentalForm(SessionBean session, ProductBean productBean) throws RentalException {
//        if (productBean == null || !productBean.getProduct().isAvailable()) {
//            throw new RentalException(StrAppSystem.get(StrAppSystem.ERR_ITEM_UNAVAILABLE));
//        }
//        return RentalController2.getRentalForm(session, productBean);
//    }

    /**
     * Invia la richiesta di noleggio compilata dal modulo al controller di business.
     * Il sistema notifica il proprietario iniettando nel backend i dettagli (Nome, telefono, email,
     * periodo, luogo consegna, note, costo del servizio e istanza articolo).
     */
    public static void sendApi(SessionBean session, RentalFormBean rentalData) throws RentalException {
        new RentalController2().saveRental(session, rentalData);
    }

    /**
     * Recupera lo storico e la coda delle richieste di noleggio associate alla sessione utente.
     */
    public static List<RentalFormBean> getUserRentals(SessionBean session) {
        return new RentalController2().getUserRentals(session);
    }

    /**
     * Consente al proprietario di accettare una richiesta PENDING.
     * Il sistema si occupa di propagare la notifica di accettazione.
     * Message di ritorno previsto: StrAppSystem.MSG_CONFIRM_ACCEPTED ("noleggio accettato").
     */
    public static void acceptRentalApi(SessionBean session, RentalFormBean rentalData) throws RentalException {
        new RentalController2().acceptRental(session, rentalData);
    }

    /**
     * Consente al proprietario di rifiutare una richiesta PENDING.
     * [Internal Step 11a]: Invia il riscontro negativo al cliente.
     * Message di ritorno previsto: StrAppSystem.MSG_CONFIRM_REJECTED ("noleggio rifiutato").
     */
    public static void rejectRentalApi(SessionBean session, RentalFormBean rentalData) throws RentalException {
        new RentalController2().rejectRental(session, rentalData);
    }

    /**
     * Consente l'annullamento o la rimozione di una pratica di noleggio in base ai privilegi del ruolo.
     */
    public static void cancelRentalApi(SessionBean session, RentalFormBean rentalData) throws RentalException {
        new RentalController2().cancelRental(session, rentalData);
    }

    /**
     * Gestisce la transazione economica del pagamento da parte del cliente.
     * A buon fine, il sistema sposta il noleggio nell'area personale e rende l'annuncio NON disponibile.
     */
    public static void payRentalApi(SessionBean session, RentalFormBean rentalData) throws RentalException {
        new RentalController2().confirmPaymentAndFinalize(session, rentalData);
    }

    /**
     * Metodo di utility/dummy adibito a test rapidi di flusso o simulazioni automatizzate dei noleggi.
     */
    public static void magicRentalApi(SessionBean session, RentalFormBean rentalData) throws RentalException {
        new RentalController2().magicRentalDummy(session, rentalData);
    }
}