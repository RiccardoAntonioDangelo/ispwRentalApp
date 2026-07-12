package org.example.view.javafx.main.test;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.view.javafx.main.MainShellContext;

public class MainShellLauncherTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Avvio del test isolato per MainShellContext...");

            // 1. Istanziamo il MainShellContext passando lo Stage principale.
            // Il costruttore si occuperà internamente di impostare la Scena (1500x800),
            // lo stile, il tema e il titolo presi da StrApp.LOGO_MAIN_TITLE.
            MainShellContext context = new MainShellContext(primaryStage);

            // 2. Facoltativo: Se vuoi testare subito una navigazione verso una route specifica
            // (ad esempio, per vedere se il container/BorderPane si aggiorna correttamente):
            // context.navigateTo(ViewRoute.BOOKING_FORM); 

            // 3. Mostriamo lo stage richiamando il metodo show() del contesto
            context.show();
            
            System.out.println("MainShellContext avviato con successo!");

        } catch (Exception e) {
            System.err.println("Errore critico durante l'avvio del test isolato di MainShellContext:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}