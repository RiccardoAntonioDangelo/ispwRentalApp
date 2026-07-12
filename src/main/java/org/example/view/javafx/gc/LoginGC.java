package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.controller.bean.LoginBean;
import org.example.exceptions.AuthenticationException;
import org.example.util.str.StrApp;
import org.example.view.GraphicAPI;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

public class LoginGC extends GraphicController<LoginGC> {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Button closeBtn;

    @FXML private Label emailLabel;
    @FXML private TextField emailField;
    @FXML private Label passLabel;
    @FXML private PasswordField passwordField;

    @FXML private Button loginBtn;

    @FXML
    public void initialize() {
        // Testi e I18n statici
        titleLabel.setText(StrApp.NAV_LOGIN);
        subtitleLabel.setText(StrApp.SIGNUP_SUBTITLE);
        closeBtn.setText(StrApp.ICON_CLOSE);

        emailLabel.setText(StrApp.SIGNUP_EMAIL);
        passLabel.setText(StrApp.SIGNUP_PASSWORD);

        loginBtn.setText(StrApp.NAV_LOGIN.toUpperCase());
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Resetta i campi sensibili ogni volta che la schermata viene ricaricata o visualizzata.
     */
    @Override
    public LoginGC updateView() {
        if (passwordField != null) {
            passwordField.clear(); // Sicurezza: svuota la password al refresh della vista
        }
        return this; // Ritorna self per il chaining fluent
    }

    @FXML
    private void handleLogin() {
        LoginBean loginData = new LoginBean(emailField.getText(),passwordField.getText());

        if (!loginData.isValid()) {
            this.alert(StrApp.LOGIN_ERROR, null, loginData.getErrorMessage());
            return;
        }
        try {
            GraphicAPI.logoutApi(this.getMainShellContext().getMemory());
            this.getMainShellContext().setMemory(GraphicAPI.loginApi(loginData));
        } catch (AuthenticationException e) {
            this.alert(StrApp.LOGIN_ERROR, null, e.getMessage());
            return;
        }
        this.goBack();
    }

    @FXML
    private void handleClose() {
        this.goBack();
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.LOGIN;
    }
}