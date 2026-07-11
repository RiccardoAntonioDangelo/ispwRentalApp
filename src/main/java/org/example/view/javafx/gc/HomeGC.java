package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.example.util.str.StrApp;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

public class HomeGC extends GraphicController<HomeGC> {

    @FXML private VBox rootContainer;

    // Icone
    @FXML private Label heroIcon;
    @FXML private Label rentIcon;
    @FXML private Label addIcon;

    // Testi Welcome
    @FXML private Label welcomeTitle;
    @FXML private Label welcomeSubtitle;

    // Card Rental
    @FXML private Label rentTitle;
    @FXML private Label rentDesc;
    @FXML private Button rentBtn;
    @FXML private Label rentRole;

    // Card Add Item
    @FXML private Label addTitle;
    @FXML private Label addDesc;
    @FXML private Button addBtn;
    @FXML private Label addRole;

    @FXML
    public void initialize() {
        // --- Setting Icone ---
        heroIcon.setText(StrApp.LOGO_MAIN_EMOJI);
        rentIcon.setText(StrApp.LOGO_CARD_RENT_EMOJI);
        addIcon.setText(StrApp.LOGO_CARD_ADD_EMOJI);

        // --- Setting Testi (da I18n / StrApp) ---
        welcomeTitle.setText(StrApp.WELCOME_TITLE);
        welcomeSubtitle.setText(StrApp.WELCOME_DESC);

        // Sezione Noleggio
        rentTitle.setText(StrApp.CARD_RENT_TITLE);
        rentDesc.setText(StrApp.CARD_RENT_DESC);
        rentBtn.setText(StrApp.BTN_RENT_START);
        rentRole.setText(StrApp.ROLE_CLIENT);

        // Sezione Aggiunta
        addTitle.setText(StrApp.CARD_ADD_TITLE);
        addDesc.setText(StrApp.CARD_ADD_DESC);
        addBtn.setText(StrApp.BTN_ADD_START);
        addRole.setText(StrApp.ROLE_OWNER);
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Gestisce il refresh dinamico della Home quando l'utente ci torna o cambia sessione.
     */
    @Override
    public HomeGC updateView() {//TODO
        // Se in futuro avrai dati dinamici in Home (es. "Benvenuto, [Nome Utente]"),
        // la logica di recupero dalla memoria andrà inserita esattamente qui.
        return this; // Permette il chaining fluent
    }

    @FXML
    private void handleAddAction() {
        // xTodo handle(null);
    }

    @FXML
    private void handleRentAction() {
        // Sfrutta l'updateView anche sulla pagina di destinazione per caricare i dati freschi
        nextPage(new CatalogGC().initViewLoader().updateView());
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.HOME;
    }
}