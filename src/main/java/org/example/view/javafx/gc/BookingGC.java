package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.controller.bean.RentalFormBean;
import org.example.controller.bean.SessionBean;
import org.example.exceptions.RentalException;
import org.example.util.str.StrApp;
import org.example.view.GraphicAPI;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;
import org.example.controller.bean.ProductBean;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingGC extends GraphicController<BookingGC> {

    @FXML private Label personalTitleLabel;
    @FXML private Label nameLabel;
    @FXML private TextField nameField;
    @FXML private Label surnameLabel;
    @FXML private TextField surnameField;
    @FXML private Label emailLabel;
    @FXML private TextField emailField;
    @FXML private Label phoneLabel;
    @FXML private TextField phoneField;
    @FXML private Label pickupLabel;
    @FXML private TextField pickupField;

    @FXML private Label periodTitleLabel;
    @FXML private Label startDateLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private Label endDateLabel;
    @FXML private DatePicker endDatePicker;

    @FXML private Label summaryTitleLabel;
    @FXML private Label summaryItemLabel;
    @FXML private Label summaryItemVal;

    @FXML private Label summaryDailyPriceLabel;
    @FXML private Label summaryDailyPriceVal;

    @FXML private Label summaryDurationLabel;
    @FXML private Label summaryDurationVal;
    @FXML private Label summarySubtotalLabel;
    @FXML private Label summarySubtotalVal;
    @FXML private Label summaryTotalLabel;
    @FXML private Label summaryTotalVal;

    @FXML private Button submitBtn;

    private ProductBean product;

    @FXML
    public void initialize() {
        initStaticTexts();

        // Impostiamo date iniziali di default (Oggi e Domani)
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));

        disablePastDates(startDatePicker);
        disableDatesBefore(endDatePicker, startDatePicker.getValue());

        // Listener per aggiornare i vincoli del calendario e ricalcolare i prezzi
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                disableDatesBefore(endDatePicker, newValue);
                if (endDatePicker.getValue() != null && endDatePicker.getValue().isBefore(newValue)) {
                    endDatePicker.setValue(newValue.plusDays(1));
                }
                updateDynamicPrices();
            }
        });

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateDynamicPrices();
            }
        });
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Gestisce il popolamento e l'aggiornamento dei dati a schermo.
     */
    @Override
    public BookingGC updateView() {
        // 1. Aggiorna i dati del prodotto se presente
        if (this.product != null) {
            updateUiWithProduct();
        }

//        // 2. Pre-popola i campi anagrafici prendendo l'utente dalla sessione in memoria
//        if (this.getMainShellContext() != null && this.getMainShellContext().getMemory() != null) {
//            SessionBean session = this.memory();
//            if (session != null && session.getSession() != null && session.getSession().getUser() != null) {
//                var user = session.getSession().getUser();
//                if (user.getName() != null) nameField.setText(user.getName());
//                if (user.getSurname() != null) surnameField.setText(user.getSurname());
//                if (user.getEmail() != null) emailField.setText(user.getEmail());
//                if (user.getPhone() != null) phoneField.setText(user.getPhone());
//            }
//        }

        // 3. Calcola i prezzi finali
        updateDynamicPrices();

        return this; // Ritorna self per il chaining fluent
    }

    /**
     * Calcola la durata e aggiorna le label dei prezzi in base alle date selezionate.
     */
    private void updateDynamicPrices() {
        if (product == null || startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            return;
        }

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        long days = ChronoUnit.DAYS.between(start, end);
        if (days <= 0) days = 1;

        double dailyPrice = product.getProduct().getPrice();
        double totalPrice = dailyPrice * days;

        summaryDailyPriceVal.setText(String.format(StrApp.BOOKING_CURRENCY_FORMAT, dailyPrice));
        summaryDurationVal.setText(days + (days == 1 ? StrApp.BOOKING_DURATION_SINGLE : StrApp.BOOKING_DURATION_PLURAL));
        summarySubtotalVal.setText(String.format(StrApp.BOOKING_CURRENCY_FORMAT, totalPrice));
        summaryTotalVal.setText(String.format(StrApp.BOOKING_CURRENCY_FORMAT, totalPrice));
    }

    private void disablePastDates(DatePicker picker) {
        picker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });
    }

    private void disableDatesBefore(DatePicker picker, LocalDate minDate) {
        picker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(minDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });
    }

    public BookingGC setProduct(ProductBean product) {
        this.product = product;
        return this; // Lasciamo che sia updateView() a gestire il refresh grafico
    }

    private void initStaticTexts() {
        personalTitleLabel.setText(StrApp.BOOKING_PERSONAL_TITLE);
        nameLabel.setText(StrApp.BOOKING_PERSONAL_NAME);
        surnameLabel.setText(StrApp.BOOKING_PERSONAL_SURNAME);
        emailLabel.setText(StrApp.BOOKING_PERSONAL_EMAIL);
        phoneLabel.setText(StrApp.BOOKING_PERSONAL_PHONE);
        pickupLabel.setText(StrApp.BOOKING_PERSONAL_PICKUP);

        emailField.setPromptText(StrApp.BOOKING_MOCK_EMAIL);
        phoneField.setPromptText(StrApp.BOOKING_MOCK_PHONE);
        pickupField.setPromptText(StrApp.BOOKING_MOCK_PICKUP);

        periodTitleLabel.setText(StrApp.BOOKING_PERIOD_TITLE);
        startDateLabel.setText(StrApp.BOOKING_PERIOD_START);
        endDateLabel.setText(StrApp.BOOKING_PERIOD_END);

        summaryTitleLabel.setText(StrApp.BOOKING_SUMMARY_TITLE);
        summaryItemLabel.setText(StrApp.BOOKING_SUMMARY_ITEM);
        summaryDailyPriceLabel.setText(StrApp.BOOKING_SUMMARY_DAILY_PRICE);
        summaryDurationLabel.setText(StrApp.BOOKING_SUMMARY_DURATION);
        summarySubtotalLabel.setText(StrApp.BOOKING_SUMMARY_SUBTOTAL);
        summaryTotalLabel.setText(StrApp.BOOKING_SUMMARY_TOTAL);

        submitBtn.setText(StrApp.BOOKING_BTN_SUBMIT);
    }

    private void updateUiWithProduct() {
        summaryItemVal.setText(product.getTitle());
        nameField.setPromptText(StrApp.BOOKING_PROMPT_NAME);
        surnameField.setPromptText(StrApp.BOOKING_PROMPT_SURNAME);
    }

    @FXML
    private void handleSubmit() {
        RentalFormBean rentalData = new RentalFormBean()
                .setOwnerEmail(this.product.getOwnerEmail())
                .setProductId(this.product.getId())
                .setName(nameField.getText())
                .setSurname(surnameField.getText())
                .setEmail(emailField.getText())
                .setPhone(phoneField.getText())
                .setPickupLocation(pickupField.getText())
                .setStart(startDatePicker.getValue())
                .setEnd(endDatePicker.getValue());

        if (!rentalData.validateAndFill()) {
            this.alert(StrApp.BOOKING_FORM, null, rentalData.getErrorMessage());
            return;
        }

        try {
            GraphicAPI.sendApi((SessionBean) this.memory(), rentalData);
        } catch (RentalException e) {
            this.alert(StrApp.SEND_ERROR, null, e.getMessage());
            return;
        }

        // Sfrutta il chaining anche sulla pagina di successo!
        this.nextPage(new SuccessGC().setRental(rentalData).initViewLoader().updateView());
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.BOOKING;
    }
}