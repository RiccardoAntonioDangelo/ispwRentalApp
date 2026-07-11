package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.util.str.StrApp;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;
import org.example.controller.bean.ProductBean;

public class ProductDetailGC extends GraphicController<ProductDetailGC> {

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
    public void initialize() {
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

    private void updateUiWithProduct() {
        specsList.getChildren().clear();
        conditionsList.getChildren().clear();

        // Lettura e riempimento UI guidati unicamente dalla lista dettagli
        for (String detail : product.getCompleteDetails()) {
            String prefix = getPrefixOf(detail);
            String value = detail.replace(prefix, "");

            // MODIFICA: Struttura Switch Expression moderna di Java
            switch (prefix) {
                case StrApp.PREFIX_TITLE -> titleLabel.setText(value);
                case StrApp.PREFIX_SUBTITLE -> subtitleLabel.setText(value);
                case StrApp.PREFIX_DAILY_PRICE -> priceValueLabel.setText(value);
                case StrApp.PREFIX_TOTAL_PRICE -> totalPriceLabel.setText(value);
                case StrApp.PREFIX_DISCOUNT -> discountTag.setText("-" + value + StrApp.TAG_SPECIAL_SUFFIX);

                case StrApp.PREFIX_SPECS -> {
                    for (String spec : value.split(", ")) {
                        if (!spec.trim().isEmpty()) addSpecItem(spec);
                    }
                }
                case StrApp.PREFIX_CONDITIONS -> {
                    for (String condition : value.split(", ")) {
                        if (!condition.trim().isEmpty()) addConditionItem(condition);
                    }
                }
                default -> { /* Ignora prefissi non impattanti sulla grafica attuale (es. ID, OWNER) */ }
            }
        }

        if (specsList.getChildren().isEmpty()) {
            addSpecItem(StrApp.FALLBACK_SPECS);
        }
        if (conditionsList.getChildren().isEmpty()) {
            addConditionItem(StrApp.FALLBACK_CONDITIONS);
        }
    }

    /**
     * MODIFICA: Metodo di supporto per isolare il prefisso della stringa per lo switch
     */
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

    private void addSpecItem(String text) {
        Label label = new Label(StrApp.DOT + text);
        label.getStyleClass().addAll("size-14", "text-hi");
        specsList.getChildren().add(label);
    }

    private void addConditionItem(String text) {
        Label label = new Label(StrApp.TRUE + text);
        label.getStyleClass().addAll("size-14", "text-hi");
        conditionsList.getChildren().add(label);
    }

    @FXML
    private void handleBooking() {
        this.nextPage(new BookingGC().setProduct(product).initViewLoader().updateView());
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.PRODUCT_DETAIL;
    }
}