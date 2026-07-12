package org.example.controller.application;

import org.example.controller.bean.CatalogBean;
import org.example.controller.bean.ProductBean;
import org.example.controller.bean.RentalFormBean;
import org.example.controller.bean.SessionBean;
import org.example.controller.application.login.LoginController;
import org.example.controller.application.login.RegisterController;
import org.example.exceptions.RentalException;
import org.example.model.dao.DAOManager;
import org.example.model.entity.rental.StatusEnum;
import org.example.model.services.CollectionI;
import org.example.model.services.EntityI;
import org.example.model.services.product.ProductI;
import org.example.model.services.rent.ActionsClientRentI;
import org.example.model.services.rent.ActionsOwnerRentI;
import org.example.model.services.rent.ActionsRentI;
import org.example.model.services.rent.RentI;
import org.example.model.services.session.SessionI;
import org.example.util.observer.ObserverI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RentalController implements LoginController, RegisterController {
    private RentalController() {
        /* This utility class should not be instantiated */
    }


    /**
     * FASE 1: Mostra tutti gli articoli disponibili nel catalogo.
     */
    public static CatalogBean getCatalog() {
        List<? extends ProductI> products = DAOManager.getProductDAO().getAll();
        CatalogBean catalogBean = new CatalogBean();
        if (products != null) {
            for (ProductI product : products) {
                if (product.isAvailable()) {
                    catalogBean.addProduct(new ProductBean(product));
                }
            }
        }
        return catalogBean;
    }

    /**
     * FASE 3: Il cliente invia la richiesta compilata.
     * Salva il noleggio sia nella sessione del cliente che in quella (se attiva) dell'owner.
     */
    public static void saveRental(SessionBean sessionBean, RentalFormBean rentalFormBean) throws RentalException {
        if (!LoginController.isAuthenticated(sessionBean)) {
            throw new RentalException("Autenticazione richiesta per completare il noleggio.");
        }
        rentalFormBean.setEmail(sessionBean.getUser());
        if ( !rentalFormBean.validateAndFill()) {
            throw new RentalException( rentalFormBean.getErrorMessage());
        }
        RentI rental = rentalFormBean.getRental();
        SessionI clientSession = sessionBean.getSession();
        SessionI ownerSession = DAOManager.getSessionDAO().getById(rental.getOwnerEmail());
        rental.attach((ObserverI<EntityI<String>>) DAOManager.getRentalDAO());
        ActionsRentI.start(
                    clientSession,
                    ownerSession,
                    rental
                    );
    }


    /**
     * Metodo privato di utilità che centralizza tutti i controlli di sicurezza e validazione
     * ed esegue l'azione passata come parametro.
     */
    private static void opRental(SessionBean sessionBean, RentalFormBean rentalFormBean, Consumer<RentI> action) throws RentalException {
        if (!LoginController.isAuthenticated(sessionBean)) {
            throw new RentalException("Autenticazione richiesta per gestire il noleggio.");
        }

        if (rentalFormBean == null) {
            throw new RentalException("Modulo di noleggio nullo.");
        }

        rentalFormBean.validateAndFill();
        RentI rental = rentalFormBean.getRental();

        if (rental == null) {
            throw new RentalException("Modulo di noleggio non valido.");
        }

        if (sessionBean.getSession().getRole() instanceof ActionsOwnerRentI) {
            action.accept(rental);
            return;
        }

        throw new RentalException("L'utente corrente non possiede i privilegi da proprietario.");
    }

    /**
     * FASE 4a: Il proprietario accetta la richiesta di noleggio.
     */
    public static void  acceptRental(SessionBean sessionBean, RentalFormBean rentalFormBean) throws RentalException {
        if(sessionBean.getSession().getRole() instanceof ActionsOwnerRentI ownerRentI ){
            opRental(sessionBean, rentalFormBean, ownerRentI::acceptRent);
        }else {
            throw new RentalException("L'utente corrente non possiede i privilegi da proprietario.");
        }

    }

    /**
     * FASE 4b: Il proprietario rifiuta la richiesta di noleggio.
     */
    public static void rejectRental(SessionBean sessionBean, RentalFormBean rentalFormBean) throws RentalException {
        if(sessionBean.getSession().getRole() instanceof ActionsOwnerRentI ownerRentI ){
            opRental(sessionBean, rentalFormBean, ownerRentI::rejectRent);
        }else {
            throw new RentalException("L'utente corrente non possiede i privilegi da proprietario.");
        }
    }
    /**
     * FASE 5: Il cliente effettua il pagamento e finalizza.
     */
    public static void confirmPaymentAndFinalize(SessionBean sessionBean, RentalFormBean rentalFormBean) throws RentalException {
        if (!LoginController.isAuthenticated(sessionBean)) {
            throw new RentalException("Autenticazione richiesta per finalizzare il pagamento.");
        }
        if (rentalFormBean == null || rentalFormBean.getRental() == null) {
            throw new RentalException("Dati di noleggio non validi.");
        }

        RentI rental = rentalFormBean.getRental();
        SessionI session = sessionBean.getSession();

        if (session.getRole() instanceof ActionsClientRentI clientActions) {
            clientActions.payRent(rental, rentalFormBean.getPayment());
            ProductI product = DAOManager.getProductDAO().getById(rental.getProductId());
            if (product == null) {
                throw new RentalException("Articolo di riferimento non trovato nel sistema.");
            }
            product.setAvailable(false);
        } else {
            throw new RentalException("L'utente corrente non può eseguire azioni di pagamento.");
        }
    }

    /**
     * Carica i noleggi in base al ruolo dell'utente (Client o Owner) 
     * sfruttando il Polling Pigro centralizzato per i contratti scaduti.
     */
    public static List<RentalFormBean> getUserRentals(SessionBean sessionBean) {
        if (sessionBean == null || sessionBean.getSession() == null) {
            return new ArrayList<>();
        }
        SessionI session = sessionBean.getSession();
        List<RentalFormBean> combinedList = new ArrayList<>();

        // Se l'utente agisce come Cliente, prendi i suoi noleggi da cliente
        if (session.getRole() instanceof ActionsClientRentI clientActions) {
            CollectionI<RentI> clientRents = clientActions.getClientRents(session);
            if (clientRents != null) {
                for (RentI rent : clientRents) {
                    ActionsRentI.completeIfExpired(rent); // Polling pigro centralizzato statico
                }
                combinedList.addAll(convertToBeanList(clientRents));
            }
        }

        // Se l'utente agisce ANCHE (o solo) come Proprietario, unisci i suoi noleggi da owner
        if (session.getRole() instanceof ActionsOwnerRentI ownerActions) {
            CollectionI<RentI> ownerRents = ownerActions.getOwnerRents(session);
            if (ownerRents != null) {
                for (RentI rent : ownerRents) {
                    ActionsRentI.completeIfExpired(rent); // Polling pigro centralizzato statico
                }
                combinedList.addAll(convertToBeanList(ownerRents));
            }
        }

        return combinedList;
    }

    private static List<RentalFormBean> convertToBeanList(CollectionI<RentI> rents) {
        List<RentalFormBean> beans = new ArrayList<>();
        if (rents == null) return beans;
        for (RentI item : rents) {
            beans.add(new RentalFormBean(item));
        }
        return beans;
    }

    /**
     * Annulla o rimuove un noleggio.
     */
    public static void cancelRental(SessionBean sessionBean, RentalFormBean rentalFormBean) throws RentalException {
        if (sessionBean == null || sessionBean.getSession() == null || rentalFormBean == null) {
            throw new RentalException("Impossibile annullare: sessione o modulo nulli.");
        }//todo non funziona
        SessionI session = sessionBean.getSession();
        RentI rental = rentalFormBean.getRental();
        boolean delete=false;
        if(rental.getStatus().equals(StatusEnum.CANCELLED))delete=true;
        if (!ActionsRentI.cancelRent(session,rental)) throw new RentalException("Impossibile annullare il noleggio nello stato attuale.");
        ProductI product = DAOManager.getProductDAO().getById(rental.getProductId());
        product.setAvailable(true);
        if(delete)DAOManager.getRentalDAO().delete(rental.getId());

    }

    public static void magicRentalDummy(SessionBean sessionBean, RentalFormBean rentalFormBean) throws RentalException {
        if(sessionBean.getSession().getRole() instanceof ActionsOwnerRentI ){
            RentI rental =rentalFormBean.getRental();
            rental.setEndDate(rental.getStartDate().minusDays(1));
        }else {
            throw new RentalException("L'utente corrente non possiede i privilegi da proprietario.");
        }
    }
}