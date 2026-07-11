package org.example.controller.interfaccia;

import org.example.controller.bean.CatalogBean;
import org.example.controller.bean.ProductBean;
import org.example.controller.bean.RentalFormBean;
import org.example.controller.bean.SessionBean;
import org.example.exceptions.RentalException;
import org.example.model.entity.session.Session;
import java.util.List;

public interface ClientRentalControllerI {

    // FASE 1: Sfogliare i prodotti disponibili
    CatalogBean getCatalog();

    // FASE 2: Richiedere il modulo pre-compilato di un articolo
    RentalFormBean getRentalForm(SessionBean session, ProductBean productBean) throws RentalException;

    // FASE 3: Inviare la richiesta di noleggio (Messaggio al proprietario)
    boolean saveRental(SessionBean session, RentalFormBean rentalFormBean) throws RentalException;

    // FASE 5: Effettuare il pagamento e rendere l'annuncio non disponibile
    void confirmPaymentAndFinalize(SessionBean session, RentalFormBean rentalFormBean) throws RentalException;

    // Visualizzare lo stato dei propri noleggi nell'area personale
    List<RentalFormBean> getUserRentals(Session session);

    // Annullare o rimuovere una propria richiesta
    void cancelRental(Session session, RentalFormBean rentalFormBean) throws RentalException;
}