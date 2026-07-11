package org.example.view.javafx.main;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.view.javafx.util.Style;
import org.example.util.str.StrApp;

public class MainShellView {

    private final Stage stage;
    private final BorderPane rootContainer;

    public MainShellView(Stage stage) {
        this.stage = stage;
        this.rootContainer = new BorderPane();
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(rootContainer, 1500, 800);
        Style.applyMainStyle(scene);
        Style.setTheme(scene, true);

        stage.setScene(scene);
        stage.setTitle(StrApp.LOGO_MAIN_TITLE);
        stage.setResizable(true);
    }

    // Cambia la grafica al centro (Nessuna logica di navigazione, accetta un nodo puro)
    public void setCenterView(Node node) {
        this.rootContainer.setCenter(node);
    }

    // Imposta la grafica in alto (Top)
    public void setTopView(Node node) {
        this.rootContainer.setTop(node);
    }


    public BorderPane getRootContainer() {
        return rootContainer;
    }

    public void show() {
        stage.show();
    }
}