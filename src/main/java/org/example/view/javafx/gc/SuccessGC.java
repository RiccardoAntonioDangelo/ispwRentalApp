package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.controller.bean.RentalFormBean;
import org.example.util.str.StrApp;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

public class SuccessGC extends GraphicController<SuccessGC> {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Label requestLabel;
    @FXML private Label requestValueLabel;
    @FXML private Button homeBtn;

    // Riferimento per salvare i dati della prenotazione completata
    private RentalFormBean rentalData;

    @FXML
    public void initialize() {
        // 1. Impostiamo i testi statici da StrApp
        titleLabel.setText(StrApp.SUCCESS_TITLE);
        messageLabel.setText(StrApp.SUCCESS_MESSAGE);
        requestLabel.setText(StrApp.SUCCESS_REQUEST_NUMBER);
        homeBtn.setText(StrApp.SUCCESS_BTN_HOME);
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Gestisce il popolamento e il refresh dei nodi grafici dinamici della schermata di successo.
     */
    @Override
    public SuccessGC updateView() {
        if (this.rentalData != null) {
            updateUiWithRental();
        }
        return this; // Consente il chaining fluent
    }

    /**
     * Riceve il bean dei dati di noleggio dal BookingFormController (Fluent API).
     */
    public SuccessGC setRental(RentalFormBean rentalData) {
        this.rentalData = rentalData;
        return this; // Lasciamo il controllo del refresh grafico a updateView()
    }

    /**
     * Estrae i dati dal bean salvato e aggiorna i campi dinamici della schermata.
     */
    private void updateUiWithRental() {
        if (rentalData == null) return;

        // Se l'entità Rental genera già un ID o un codice di richiesta, lo prendiamo da lì.
        // Se non hai ancora l'ID reale dal DB, puoi generarlo dinamicamente o usare un metodo del bean. todo
        if (rentalData.getRental() != null && rentalData.getRental().getId() != null) {
            requestValueLabel.setText("#" + rentalData.getRental().getId());
        } else {
            // Fallback temporaneo se il codice non è ancora generato dal database
            requestValueLabel.setText("#NOL-" + System.currentTimeMillis() % 10000);
        }
    }

    @FXML
    private void handleHome() {
        backHome();
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.SUCCESS;
    }
}