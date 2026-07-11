package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.controller.bean.CatalogBean;
import org.example.controller.bean.ProductBean;
import org.example.util.str.StrApp;
import org.example.view.javafx.componet.ProductCardGC;
import org.example.view.GraphicAPI; // Import della GraphicAPI
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

public class CatalogGC extends GraphicController<CatalogGC> {

    @FXML private Label filterTitleLabel;
    @FXML private TextField searchField;
    @FXML private Button categoryBtn;
    @FXML private Button priceBtn;
    @FXML private GridPane productGrid;

    @FXML
    public void initialize() {
        filterTitleLabel.setText(StrApp.FILTER_TITLE);
        searchField.setPromptText(StrApp.FILTER_SEARCH_PROMPT);
        categoryBtn.setText(StrApp.FILTER_CATEGORY);
        priceBtn.setText(StrApp.FILTER_PRICE);
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Recupera i dati del catalogo aggiornati ed esegue il refresh della griglia.
     */
    @Override
    public CatalogGC updateView() {
        // MODIFICA: I dati ora arrivano direttamente dalla GraphicAPI
        CatalogBean catalog = GraphicAPI.getCatalog();
        loadCatalogData(catalog);
        return this; // Consente il chaining fluent
    }

    public void loadCatalogData(CatalogBean catalogBean) {
        productGrid.getChildren().clear();
        if (catalogBean == null || catalogBean.getProducts().isEmpty()) {
            Label emptyLabel = new Label(StrApp.CATALOG_EMPTY_MESSAGE);
            productGrid.add(emptyLabel, 0, 0);
            return;
        }

        int column = 0;
        int columnMax = 4;
        int row = 0;

        for (ProductBean bean : catalogBean.getProducts()) {
            Node cardNode = ProductCardGC.createCard(bean, () -> nextPage(new ProductDetailGC().setProduct(bean).initViewLoader().updateView()));
            productGrid.add(cardNode, column, row);
            column++;
            if (column == columnMax) {
                column = 0;
                row++;
            }
        }
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.RENTAL;
    }
}