package org.example.view.javafx.componet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.view.javafx.util.Style;
import org.example.util.str.StrApp;
import org.example.view.javafx.gc.SettingGC;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

import java.util.List;
import java.util.function.Supplier;

public class HeaderGC extends GraphicController<HeaderGC> {

    private final GraphicController<?> settingsController = new SettingGC().initViewLoader();

    @FXML private Label logoEmoji;
    @FXML private Label logoTitle;
    @FXML private Label logoSubtitle;
    @FXML private HBox headerButtonsContainer;
    @FXML private HBox breadcrumbContainer;
    @FXML private Button settingsBtn;

    @FXML
    public void initialize() {
        logoEmoji.setText(StrApp.LOGO_MAIN_EMOJI);
        logoTitle.setText(StrApp.LOGO_MAIN_TITLE);
        logoSubtitle.setText(StrApp.LOGO_SUBTITLE);
        settingsBtn.setText(StrApp.ICON_SETTINGS);
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Ritorna self per non interrompere il chaining.
     */
    @Override
    public HeaderGC updateView() {
        return this;
    }

    /**
     * Riceve la cronologia dei javafx e aggiorna la grafica visualizzandoli al contrario
     */
    public void updateBreadcrumbs(List<GraphicController<?>> history) {
        breadcrumbContainer.getChildren().clear();
        if (history == null || history.isEmpty()) return;

        // 1. Il ciclo parte dall'ultimo elemento della cronologia (il più recente)
        //    e si ferma PRIMA dell'ultimo (i > 0), creandoli come Bottoni
        for (int i = history.size() - 1; i > 0; i--) {
            GraphicController<?> controller = history.get(i);
            ViewRoute route = controller.getRoute();
            if (route == null) continue;

            String stepName = route.getTitle();

            Button btn = new Button(stepName);
            btn.getStyleClass().add(Style.BTN_BREADCRUMB);
            btn.setOnAction(e -> handle(controller));
            breadcrumbContainer.getChildren().add(btn);

            // Separatore classico
            Label sep = new Label(" > ");
            sep.getStyleClass().add(Style.TEXT_LO);
            breadcrumbContainer.getChildren().add(sep);
        }

        // 2. L'elemento più vecchio (l'indice 0, la radice iniziale)
        //    viene stampato alla fine come Label attiva non cliccabile
        GraphicController<?> rootController = history.getFirst();
        if (rootController != null && rootController.getRoute() != null) {
            Label label = new Label(rootController.getRoute().getTitle());
            label.getStyleClass().add(Style.BREADCRUMB_ACTIVE);
            breadcrumbContainer.getChildren().add(label);
        }
    }

    public void addActionButton(String label, EventHandler<ActionEvent> action) {
        Button btn = new Button(label);
        btn.getStyleClass().add(Style.BTN_NAV);
        btn.setOnAction(action);
        headerButtonsContainer.getChildren().addFirst(btn);
    }
    /**
     * 🆕 Aggiunge un bottone toggle dinamico che scambia testo e azione in modo incrociato.
     * Puoi chiamarlo quante volte vuoi per aggiungere più bottoni toggle nel container.
     */
    public void addActionButtonToggle(Supplier<String> textProvider,
                                      EventHandler<ActionEvent> loginAction,
                                      EventHandler<ActionEvent> logoutAction) {
        Button btn = new Button(textProvider.get());
        btn.getStyleClass().add(Style.BTN_NAV);

        // Salviamo il textProvider nel bottone stesso tramite UserData per poterlo rinfrescare in updateView()
        btn.setUserData(textProvider);

        btn.setOnAction(e -> {
            String currentText = btn.getText();

            // Logica incrociata basata sul testo corrente del bottone
            if (currentText.equals(StrApp.get(StrApp.NAV_LOGIN))) {
                loginAction.handle(e);
            } else {
                logoutAction.handle(e);
            }

            // Aggiorna immediatamente il testo dopo il click
            btn.setText(textProvider.get());
        });
        headerButtonsContainer.getChildren().addFirst(btn);
    }

    public void clearActionButtons() {
        headerButtonsContainer.getChildren().removeIf(node ->
                node instanceof Button && node.getStyleClass().contains(Style.BTN_NAV)
        );
    }

    @FXML
    private void handleSettings() {
        handle(settingsController);
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.HEADER;
    }

    public void handle(GraphicController<?> controller) {
        if (this.getMainShellContext() != null) {
            this.nextPage(controller);
        }
    }

}