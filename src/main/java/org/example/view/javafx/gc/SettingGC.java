package org.example.view.javafx.gc;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.Scene;
import org.example.view.javafx.util.Style;
import org.example.util.str.StrApp;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

public class SettingGC extends GraphicController<SettingGC> {

    @FXML private Label titleLabel;
    @FXML private Label themeLabel;
    @FXML private ToggleButton themeToggle;

    @FXML
    public void initialize() {
        titleLabel.setText(StrApp.SETTINGS_TITLE);
        themeLabel.setText(StrApp.SETTINGS_THEME_LABEL);

        Scene scene = themeToggle.getScene();
        if (scene != null) {
            updateToggleState(scene);
        } else {
            themeToggle.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) updateToggleState(newScene);
            });
        }
    }

    /**
     * IMPLEMENTAZIONE DI UPDATEVIEW (Richiesto dalla superclasse)
     * Sincronizza lo stato del toggle grafico del tema corrente.
     */
    @Override
    public SettingGC updateView() {
        Scene scene = themeToggle.getScene();
        if (scene != null) {
            updateToggleState(scene);
        }
        return this; // Consente il chaining fluent
    }

    private void updateToggleState(Scene scene) {
        boolean isDark = Style.isDark(scene);
        themeToggle.setSelected(isDark);
        themeToggle.setText(isDark ? StrApp.ICON_THEME_LIGHT : StrApp.ICON_THEME_DARK);
    }

    @FXML
    private void handleThemeToggle() {
        Scene scene = themeToggle.getScene();
        if (scene == null) return;

        boolean isDark = themeToggle.isSelected();
        themeToggle.setText(isDark ? StrApp.ICON_THEME_LIGHT : StrApp.ICON_THEME_DARK);
        Style.setTheme(scene, isDark);
    }

    @Override
    public ViewRoute getRoute() {
        return ViewRoute.SETTING;
    }
}