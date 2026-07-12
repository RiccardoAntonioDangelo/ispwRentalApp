package org.example.view.javafx.util;

import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * Classe per la gestione centralizzata dello stile e del cambio tema tramite classi CSS.
 */
public class Style {
    private Style() {
        /* This utility class should not be instantiated */
    }

    // File CSS principale (Design System)
    public static final String MAIN_STYLE = "/org/example/main.css";

    // Classi per il cambio tema
    public static final String THEME_LIGHT = "theme-light";
    public static final String THEME_DARK = "theme-dark";

    
    // Stati Ruoli
    public static final String CARD_ACTIVE = "card-active";
    public static final String CARD_INACTIVE = "card-inactive";
    public static final String DOT_ACTIVE = "dot-active";
    public static final String DOT_INACTIVE = "dot-inactive";

    // Header & Breadcrumbs
    public static final String BTN_BREADCRUMB ="btn-breadcrumb";
    public static final String TEXT_LO = "text-lo";
    public static final String BREADCRUMB_ACTIVE = "breadcrumb-active";
    public static final String BTN_NAV ="btn-breadcrumb"; //btn-nav

    /**
     * Applica il filedeprecated CSS principale alla scena.
     */
    public static void applyMainStyle(Scene scene) {
        var resource = Style.class.getResource(MAIN_STYLE);
        if (resource != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(resource.toExternalForm());
        }
    }

    /**
     * Cambia il tema aggiungendo/rimuovendo classi dal root della scena.
     */
    public static void setTheme(Scene scene, boolean isDark) {
        Node root = scene.getRoot();
        root.getStyleClass().removeAll(THEME_LIGHT, THEME_DARK);
        root.getStyleClass().add(isDark ? THEME_DARK : THEME_LIGHT);
    }

    public static boolean isDark(Scene scene) {
        return scene.getRoot().getStyleClass().contains(THEME_DARK);
    }

    public static void setRoleState(Node card, Node dot, boolean isActive) {
        card.getStyleClass().removeAll(CARD_ACTIVE, CARD_INACTIVE);
        dot.getStyleClass().removeAll(DOT_ACTIVE, DOT_INACTIVE);
        card.getStyleClass().add(isActive ? CARD_ACTIVE : CARD_INACTIVE);
        dot.getStyleClass().add(isActive ? DOT_ACTIVE : DOT_INACTIVE);
    }
}
