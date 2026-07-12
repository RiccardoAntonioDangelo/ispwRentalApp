package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.util.str.StrApp;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;
import org.example.controller.bean.ProductBean;

/**
 * Controller grafico per la visualizzazione dettagliata di un singolo prodotto.
 * Gestisce specifiche, condizioni, prezzi e l'avvio della procedura di prenotazione.
 */
public class ProductDetailGC extends GraphicController<ProductDetailGC> {

    // Costanti per evitare stringhe duplicate (DRY)
    private static final String STYLE_SIZE_14 = "size-14";
    private static final String STYLE_TEXT_HI = "text-hi";
    private static final String SPLIT_REGEX = ", ";

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label priceValueLabel;
    @FXML private Label priceUnitLabel;

    @FXML private Label specsTitleLabel;
    @FXML private VBox specsList;

    @FXML private Label warrantyTitleLabel;
    @FXML private Label warrantyDescLabel;

    @FXML private Label conditionsTitleLabel;
    @FXML private VBox conditionsList;

    @FXML private Label bookingEstLabel;
    @FXML private Label discountTag;
    @FXML private Label totalPriceLabel;
    @FXML private Button bookBtn;

    private ProductBean product;

    @FXML
    void initialize() {
        specsTitleLabel.setText(StrApp.PRODUCT_DETAIL_SPECS_TITLE);
        priceUnitLabel.setText(StrApp.PRODUCT_DETAIL_PRICE_PERDAY);
        warrantyTitleLabel.setText(StrApp.PRODUCT_DETAIL_WARRANTY_TITLE);
        warrantyDescLabel.setText(StrApp.PRODUCT_DETAIL_WARRANTY_DESC);
        conditionsTitleLabel.setText(StrApp.PRODUCT_DETAIL_CONDITIONS_TITLE);
        bookingEstLabel.setText(StrApp.PRODUCT_DETAIL_BOOKING_ESTIMATED);
        bookBtn.setText(StrApp.PRODUCT_DETAIL_BTN_BOOK);
    }

    @Override
    public ProductDetailGC updateView() {
        if (this.product != null) {
            updateUiWithProduct();
        }
        return this;
    }

    public ProductDetailGC setProduct(ProductBean product) {
        this.product = product;
        return this;
    }

    /**
     * Svuota e popola la UI. La complessità è ora minima grazie ai metodi ausiliari delegati.
     */
    private void updateUiWithProduct() {
        specsList.getChildren().clear();
        conditionsList.getChildren().clear();

        for (String detail : product.getCompleteDetails()) {
            String prefix = getPrefixOf(detail);
            String value = detail.replace(prefix, "");

            switch (prefix) {
                case StrApp.PREFIX_TITLE -> titleLabel.setText(value);
                case StrApp.PREFIX_SUBTITLE -> subtitleLabel.setText(value);
                case StrApp.PREFIX_DAILY_PRICE -> priceValueLabel.setText(value);
                case StrApp.PREFIX_TOTAL_PRICE -> totalPriceLabel.setText(value);
                case StrApp.PREFIX_DISCOUNT -> discountTag.setText("-" + value + StrApp.TAG_SPECIAL_SUFFIX);

                // Chiamata ai metodi ausiliari per azzerare la nidificazione
                case StrApp.PREFIX_SPECS -> populateSplitItems(value, specsList, StrApp.DOT);
                case StrApp.PREFIX_CONDITIONS -> populateSplitItems(value, conditionsList, StrApp.TRUE);

                default -> { /* Ignora prefissi non impattanti sulla grafica attuale */ }
            }
        }

        checkFallbacks();
    }

    /**
     * METODO AUSILIARIO: Elabora le stringhe separate da virgola (Specifiche/Condizioni)
     * e le aggiunge al rispettivo contenitore grafico senza appesantire lo switch principale.
     */
    private void populateSplitItems(String value, VBox container, String bullet) {
        for (String item : value.split(SPLIT_REGEX)) {
            if (!item.trim().isEmpty()) {
                Label label = new Label(bullet + item);
                label.getStyleClass().addAll(STYLE_SIZE_14, STYLE_TEXT_HI);
                container.getChildren().add(label);
            }
        }
    }

    /**
     * METODO AUSILIARIO: Gestisce l'inserimento dei dati di fallback se le liste sono vuote.
     */
    private void checkFallbacks() {
        if (specsList.getChildren().isEmpty()) {
            addFallbackItem(specsList, StrApp.DOT, StrApp.FALLBACK_SPECS);
        }
        if (conditionsList.getChildren().isEmpty()) {
            addFallbackItem(conditionsList, StrApp.TRUE, StrApp.FALLBACK_CONDITIONS);
        }
    }

    private void addFallbackItem(VBox container, String bullet, String text) {
        Label label = new Label(bullet + text);
        label.getStyleClass().addAll(STYLE_SIZE_14, STYLE_TEXT_HI);
        container.getChildren().add(label);
    }

    private String getPrefixOf(String detail) {
        if (detail.startsWith(StrApp.PREFIX_TITLE)) return StrApp.PREFIX_TITLE;
        if (detail.startsWith(StrApp.PREFIX_SUBTITLE)) return StrApp.PREFIX_SUBTITLE;
        if (detail.startsWith(StrApp.PREFIX_DAILY_PRICE)) return StrApp.PREFIX_DAILY_PRICE;
        if (detail.startsWith(StrApp.PREFIX_DISCOUNT)) return StrApp.PREFIX_DISCOUNT;
        if (detail.startsWith(StrApp.PREFIX_TOTAL_PRICE)) return StrApp.PREFIX_TOTAL_PRICE;
        if (detail.startsWith(StrApp.PREFIX_SPECS)) return StrApp.PREFIX_SPECS;
        if (detail.startsWith(StrApp.PREFIX_CONDITIONS)) return StrApp.PREFIX_CONDITIONS;
        return "";
    }

    @FXML
    void handleBooking() {
        this.nextPage(new BookingGC().setProduct(product).initViewLoader().updateView());
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.PRODUCT_DETAIL;
    }
}