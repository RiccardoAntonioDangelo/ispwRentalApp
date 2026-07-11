package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.controller.bean.RentalFormBean;
import org.example.util.str.StrApp;
import org.example.view.javafx.componet.RentalCardGC;
import org.example.view.GraphicAPI;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

import java.util.List;

public class UserRentalGC extends GraphicController<UserRentalGC> {

    @FXML private Label titleLabel;
    @FXML private VBox rentalsListContainer;

    @FXML
    public void initialize() {
        titleLabel.setText(StrApp.NAV_USER_RENTAL);
        updateView();
    }

    @Override
    public UserRentalGC updateView() {
        if (this.memory() != null) {
            List<RentalFormBean> rentals = GraphicAPI.getUserRentals(this.memory());
            loadRentals(rentals);
        } else {
            rentalsListContainer.getChildren().clear();
            Label emptyLabel = new Label(StrApp.ERR_INVALID_SESSION);
            emptyLabel.getStyleClass().add("text-lo");
            rentalsListContainer.getChildren().add(emptyLabel);
        }
        return this;
    }

    private void loadRentals(List<RentalFormBean> rentals) {
        rentalsListContainer.getChildren().clear();

        if (rentals == null || rentals.isEmpty()) {
            Label emptyLabel = new Label(StrApp.RENTALS_EMPTY_MESSAGE);
            emptyLabel.getStyleClass().add("text-lo");
            rentalsListContainer.getChildren().add(emptyLabel);
            return;
        }

        for (RentalFormBean rental : rentals) {
            // Utilizza direttamente il metodo factory statico pulito ed elegante della card
            rentalsListContainer.getChildren().add(
                    RentalCardGC.createCard(getContext()).setRentalData(rental, this::updateView).getView()
            );
        }
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.USER_RENTAL;
    }
}