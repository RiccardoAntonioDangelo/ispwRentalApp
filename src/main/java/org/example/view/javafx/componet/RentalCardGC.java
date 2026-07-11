package org.example.view.javafx.componet;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.controller.bean.RentalFormBean;
import org.example.exceptions.RentalException;
import org.example.util.str.StrApp;
import org.example.view.GraphicAPI;
import org.example.view.javafx.util.ViewRoute;
import org.example.view.javafx.util.state.Context;

/**
 * Controller grafico per la gestione visiva ed esecutiva dei noleggi utente all'interno di una card.
 */
public class RentalCardGC extends AbstractComponentGC<RentalCardGC> {

    @FXML private VBox cardRoot;
    @FXML private Label prodLabel;
    @FXML private Label idLabel;
    @FXML private Label statusBadge;
    @FXML private Label fromTitleLabel;
    @FXML private Label fromValueLabel;
    @FXML private Label toTitleLabel;
    @FXML private Label toValueLabel;

    @FXML private Button acceptBtn;
    @FXML private Button rejectBtn;
    @FXML private Button cancelBtn;
    @FXML private Button payBtn;
    @FXML private Button magicBtn;

    private RentalFormBean rentalBean;
    /** Callback eseguita per rinfrescare la lista del parent a seguito di un aggiornamento di stato API. */
    private Runnable onActionSuccess;

    /**
     * interfaccia funzionale locale per uniformare e comprimere la logica delle chiamate API transazionali.
     */
    @FunctionalInterface
    private interface RentalAction {
        void execute() throws RentalException;
    }

    /**
     * Factory Method statico che genera la card associandovi i dati del noleggio e la callback di refresh.
     */
    public static Parent createCard(RentalFormBean bean, Runnable onActionSuccess) {
        RentalCardGC controller = new RentalCardGC().getComponentGraphicController();
        controller.setRentalData(bean, onActionSuccess);
        return controller.getView();
    }

    /**
     * Sovraccarico del Factory Method per istanziare la card partendo da un oggetto di contesto generico.
     */
    public static RentalCardGC createCard(Context context) {
        RentalCardGC controller = new RentalCardGC().getComponentGraphicController();
        controller.setContext(context);
        return controller;
    }

    @Override
    public RentalCardGC updateView() {
        return this;
    }

    /**
     * Popola i campi grafici della card eseguendo il parsing completo delle stringhe descrittive del noleggio.
     */
    public RentalCardGC setRentalData(RentalFormBean bean, Runnable onActionSuccess) {
        this.rentalBean = bean;
        this.onActionSuccess = onActionSuccess;

        // Iniezione delle labels dei pulsanti e dei titoli ricavati da StrApp
        fromTitleLabel.setText(StrApp.RENTALS_FROM);
        toTitleLabel.setText(StrApp.RENTALS_TO);
        acceptBtn.setText(StrApp.RENTALS_BTN_ACCEPT);
        rejectBtn.setText(StrApp.RENTALS_BTN_REJECT);
        cancelBtn.setText(StrApp.RENTALS_BTN_CANCEL);
        payBtn.setText(StrApp.RENTALS_BTN_PAY);
        magicBtn.setText(StrApp.RENTALS_BTN_MAGIC);

        // Valori di fallback predefiniti in caso di assenza dati
        String productName = StrApp.TEXT_NOT_AVAILABLE;
        String start = StrApp.TEXT_NOT_AVAILABLE;
        String end = StrApp.TEXT_NOT_AVAILABLE;
        String status = StrApp.STATUS_DEFAULT_PENDING;

        String id = (bean.getRental() != null && bean.getRental().getId() != null)
                ? bean.getRental().getId()
                : StrApp.TEXT_NOT_AVAILABLE;

        // Iterazione e smistamento dinamico dei dettagli tramite prefissi strutturati
        for (String detail : bean.getCompleteDetails()) {
            String prefix = bean.getPrefixOf(detail);
            String value = detail.replace(prefix, "");

            switch (prefix) {
                case StrApp.PREFIX_ID -> productName = value;
                case StrApp.PREFIX_START_DATE -> start = value;
                case StrApp.PREFIX_END_DATE -> end = value;
                case StrApp.PREFIX_STATUS -> status = value;
                default -> {/*default PREFIX*/}
            }
        }

        // Assegnazione finale del testo ai nodi JavaFX
        prodLabel.setText(StrApp.RENTALS_PRODUCT + " " + productName);
        idLabel.setText(StrApp.RENTALS_ID + " " + id);
        statusBadge.setText(status);
        fromValueLabel.setText(start);
        toValueLabel.setText(end);
        return this;
    }

    // =========================================================================
    // --- AZIONI INTERFACCIA UTENTE FXML ---
    // =========================================================================

    @FXML private void handleMagic() { executeRentalAction(() -> GraphicAPI.magicRentalApi(this.memory(), rentalBean), StrApp.SIGNUP_ERROR); }
    @FXML private void handleAccept() { executeRentalAction(() -> GraphicAPI.acceptRentalApi(this.memory(), rentalBean), StrApp.SIGNUP_ERROR); }
    @FXML private void handleReject() { executeRentalAction(() -> GraphicAPI.rejectRentalApi(this.memory(), rentalBean), StrApp.SIGNUP_ERROR); }
    @FXML private void handleCancel() { executeRentalAction(() -> GraphicAPI.cancelRentalApi(this.memory(), rentalBean), StrApp.SEND_ERROR); }
    @FXML private void handlePay() { executeRentalAction(() -> GraphicAPI.payRentalApi(this.memory(), rentalBean), StrApp.SEND_ERROR); }

    /**
     * Centralizza la gestione dei blocchi try-catch e la conseguente notifica visiva ed emersione di alert di errore.
     */
    private void executeRentalAction(RentalAction action, String errorTitle) {
        try {
            action.execute();
            notifyParent();
        } catch (RentalException e) {
            this.alert(errorTitle, null, e.getMessage());
        } catch (Exception e) {
            this.alert(StrApp.SEND_ERROR, null, e.getMessage());
        }
    }

    private void notifyParent() {
        if (onActionSuccess != null) {
            onActionSuccess.run();
        }
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.RENTAL_CARD;
    }
}