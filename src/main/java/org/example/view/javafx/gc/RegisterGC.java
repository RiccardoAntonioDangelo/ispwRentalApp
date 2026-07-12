package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.view.javafx.util.Style;
import org.example.controller.bean.RegisterBean;
import org.example.exceptions.AuthenticationException;
import org.example.util.str.StrApp;
import org.example.view.GraphicAPI;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterGC extends GraphicController<RegisterGC> {

    // Costanti per astrarre lo spacchettamento della matrice di stringhe dei ruoli
    private static final int INDEX_ROLE_ID   = 0;
    private static final int INDEX_ROLE_NAME = 1;
    private static final int INDEX_ROLE_DESC = 2;

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label roleLabel;
    @FXML private Button closeBtn;

    @FXML private VBox rolesContainer;

    @FXML private Label emailLabel;
    @FXML private TextField emailField;
    @FXML private Label passLabel;
    @FXML private PasswordField passwordField;
    @FXML private Label confirmPassLabel;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Label demoLabel;
    @FXML private Button signupBtn;

    private String selectedRole = "";

    private final Map<String, RoleNodes> roleNodesMap = new HashMap<>();
    private record RoleNodes(VBox card, StackPane dot) {}

    @FXML
    public void initialize() {
        // Testi e I18n fissi
        titleLabel.setText(StrApp.SIGNUP_TITLE);
        subtitleLabel.setText(StrApp.SIGNUP_SUBTITLE);
        roleLabel.setText(StrApp.SIGNUP_ROLE_LABEL);
        closeBtn.setText(StrApp.ICON_CLOSE);

        emailLabel.setText(StrApp.SIGNUP_EMAIL);
        passLabel.setText(StrApp.SIGNUP_PASSWORD);
        confirmPassLabel.setText(StrApp.SIGNUP_CONFIRMPASSWORD);

        demoLabel.setText(StrApp.SIGNUP_DEMO);
        signupBtn.setText(StrApp.SIGNUP_SUBMIT);

        // Generazione iniziale della UI dei ruoli disponibili
        List<String[]> availableRoles = GraphicAPI.getAllRoles();
        buildDynamicRoles(availableRoles);

        if (!availableRoles.isEmpty()) {
            updateRoleUI(availableRoles.getFirst()[INDEX_ROLE_ID]);
        }
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Resetta i campi di immissione testo sensibili per motivi di sicurezza o al rinfresco della pagina.
     */
    @Override
    public RegisterGC updateView() {
        if (passwordField != null) {
            passwordField.clear();
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.clear();
        }
        return this; // Permette il chaining fluent
    }

    private void buildDynamicRoles(List<String[]> roles) {
        rolesContainer.getChildren().clear();
        roleNodesMap.clear();

        for (String[] role : roles) {
            // Risolto il TODO: Utilizzo delle costanti semantiche al posto degli indici numerici hardcoded
            String roleId   = role[INDEX_ROLE_ID];
            String roleName = role[INDEX_ROLE_NAME];
            String roleDesc = role[INDEX_ROLE_DESC];

            VBox card = new VBox();
            card.setSpacing(15.0);
            card.getStyleClass().addAll("rounded-md", "border-all", "cursor-hand");
            card.setPadding(new Insets(15));

            HBox hbox = new HBox(15.0);
            hbox.setAlignment(Pos.CENTER_LEFT);

            StackPane dot = new StackPane();
            dot.setPrefSize(12.0, 12.0);
            dot.getStyleClass().add("rounded-full");

            VBox textContainer = new VBox();
            Label nameLabel = new Label(roleName);
            nameLabel.getStyleClass().addAll("size-14", "font-bold", "text-hi");
            Label descLabel = new Label(roleDesc);
            descLabel.getStyleClass().addAll("size-12", "text-lo");

            textContainer.getChildren().addAll(nameLabel, descLabel);
            hbox.getChildren().addAll(dot, textContainer);
            card.getChildren().add(hbox);

            card.setOnMouseClicked(event -> updateRoleUI(roleId));

            roleNodesMap.put(roleId, new RoleNodes(card, dot));
            rolesContainer.getChildren().add(card);
        }
    }

    private void updateRoleUI(String roleId) {
        selectedRole = roleId;
        roleNodesMap.forEach((id, nodes) -> {
            boolean isSelected = id.equals(selectedRole);
            Style.setRoleState(nodes.card(), nodes.dot(), isSelected);
        });
    }

    @FXML
    private void handleSignup() {
        RegisterBean registerData = new RegisterBean(selectedRole,emailField.getText(),passwordField.getText(),confirmPasswordField.getText());
        if (!registerData.isValid()) {
            this.alert(StrApp.SIGNUP_ERROR, null, registerData.getErrorMessage());
            return;
        }
        try {
            GraphicAPI.logoutApi(this.getMainShellContext().getMemory());
            this.getMainShellContext().setMemory(GraphicAPI.registerApi(registerData));
        } catch (AuthenticationException e) {
            this.alert(StrApp.SIGNUP_ERROR, null, e.getMessage());
        }
        this.goBack();
    }

    @FXML
    private void handleClose() {
        this.goBack();
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.REGISTER;
    }
}