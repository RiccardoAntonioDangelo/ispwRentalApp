package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.view.javafx.main.MainShellContext;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
       DAOManager.initializeSingleton(EnumDaoType.DEMO,true,true);
       new MainShellContext(stage).show();
       new MainShellContext(new Stage()).show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}

