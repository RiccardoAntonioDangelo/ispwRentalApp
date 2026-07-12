package org.example.view.javafx.componet;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.controller.bean.ProductBean;
import org.example.util.str.StrApp;
import org.example.view.javafx.util.ViewRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller grafico per la rappresentazione visiva di un prodotto all'interno di una card riutilizzabile.
 */
public class ProductCardGC extends AbstractComponentGC<ProductCardGC> {

    // Costante introdotta per eliminare il warning della stringa ripetuta (SonarQube compliant)
    private static final String STYLE_SIZE_13 = "size-13";

    @FXML private VBox cardRoot;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label badgeLabel;
    @FXML private Label fromLabel;
    @FXML private Label priceValueLabel;
    @FXML private Button detailsBtn;

    /** Callback da eseguire quando l'utente preme sul pulsante dei dettagli. */
    private Runnable onDetailsClicked;

    /**
     * Factory Method statico per generare in modo pulito il nodo grafico Parent del Prodotto.
     * Sfrutta l'istanza astratta per caricare l'FXML.
     *
     * @param bean             Il Bean contenente i dati del prodotto da mostrare.
     * @param onDetailsClicked L'azione da eseguire al click sul pulsante dei dettagli.
     * @return Il nodo grafico {@link Parent} pronto per essere inserito nella UI.
     */
    public static Parent createCard(ProductBean bean, Runnable onDetailsClicked) {
        ProductCardGC controller = new ProductCardGC().getComponentGraphicController();
        controller.setProductData(bean, onDetailsClicked);
        return controller.getView();
    }

    /**
     * Implementazione obbligatoria per aggiornare lo stato del componente.
     * Ritorna l'istanza corrente (Fluent Interface).
     *
     * @return L'istanza stessa del controller.
     */
    @Override
    public ProductCardGC updateView() {
        return this;
    }

    /**
     * Inietta i dati del prodotto all'interno dei nodi JavaFX e mappa la logica visiva condizionale.
     *
     * @param bean             Il Bean con le informazioni aggiornate.
     * @param onDetailsClicked Il callback per l'evento di navigazione/dettaglio.
     */
    public void setProductData(ProductBean bean, Runnable onDetailsClicked) {
        this.onDetailsClicked = onDetailsClicked;

        List<String> midElements = new ArrayList<>();

        for (String detail : bean.getReducedDetails()) {
            String prefix = getPrefixOf(detail);
            String value = detail.replace(prefix, "");

            switch (prefix) {
                case StrApp.PREFIX_TITLE -> titleLabel.setText(value);
                case StrApp.PREFIX_DAILY_PRICE -> priceValueLabel.setText(value);
                case StrApp.PREFIX_SUBTITLE -> midElements.add(value);
                case StrApp.PREFIX_DISCOUNT -> midElements.add(StrApp.LABEL_DISCOUNT_PREFIX + value);
                case StrApp.PREFIX_TOTAL_PRICE -> midElements.add(StrApp.LABEL_WEEKLY_PRICE_PREFIX + value);
                default -> { /* Ignora prefissi non impattanti sul layout compatto della card */ }
            }
        }

        // Gestione dinamica visiva/strutturale del badge multi-linea centrale
        if (!midElements.isEmpty()) {
            badgeLabel.setText(String.join("\n", midElements));
            badgeLabel.setVisible(true);
            badgeLabel.setManaged(true);
        } else {
            badgeLabel.setVisible(false);
            badgeLabel.setManaged(false);
        }

        fromLabel.setText(StrApp.PRODUCT_PRICE_FROM);
        detailsBtn.setText(StrApp.BTN_DETAILS);

        // Reset e applicazione degli stili CSS usando la nuova costante
        subtitleLabel.getStyleClass().removeAll(STYLE_SIZE_13, "text-accent", "text-lo");

        if (bean.getProduct().isAvailable()) {
            subtitleLabel.setText(StrApp.PRODUCT_STATUS_AVAILABLE);
            subtitleLabel.getStyleClass().addAll(STYLE_SIZE_13, "text-accent");
            cardRoot.setOpacity(1.0);
        } else {
            subtitleLabel.setText(StrApp.PRODUCT_STATUS_UNAVAILABLE);
            subtitleLabel.getStyleClass().addAll(STYLE_SIZE_13, "text-lo");
            cardRoot.setOpacity(0.6);
        }
    }

    /**
     * Mappa ed estrae in sicurezza il prefisso della stringa di dettaglio per l'uso nello switch.
     */
    private String getPrefixOf(String detail) {
        if (detail.startsWith(StrApp.PREFIX_TITLE)) {
            return StrApp.PREFIX_TITLE;
        }
        if (detail.startsWith(StrApp.PREFIX_SUBTITLE)) {
            return StrApp.PREFIX_SUBTITLE;
        }
        if (detail.startsWith(StrApp.PREFIX_DAILY_PRICE)) {
            return StrApp.PREFIX_DAILY_PRICE;
        }
        if (detail.startsWith(StrApp.PREFIX_DISCOUNT)) {
            return StrApp.PREFIX_DISCOUNT;
        }
        if (detail.startsWith(StrApp.PREFIX_TOTAL_PRICE)) {
            return StrApp.PREFIX_TOTAL_PRICE;
        }
        return "";
    }

    /**
     * Gestisce l'azione di click sul pulsante dei dettagli.
     */
    @FXML
    void onDetailsAction() {
        if (onDetailsClicked != null) {
            onDetailsClicked.run();
        }
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.PRODUCT_CARD;
    }
}