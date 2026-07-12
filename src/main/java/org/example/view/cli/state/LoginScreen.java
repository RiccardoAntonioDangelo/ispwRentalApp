package org.example.view.cli.state;

import org.example.controller.bean.LoginBean;
import org.example.exceptions.AuthenticationException;
import org.example.view.GraphicAPI;
import org.example.view.cli.context.CliContext;

public class LoginScreen extends AbstractCliScreen {

    public LoginScreen(CliContext context) {
        super(context);
    }

    @Override
    public CliScreenState handleAction() {
        var out = context.getOut();
        var scanner = context.getScanner();

        out.println("\n--- Schermata di Login ---");
        out.print("Inserisci Username: ");
        String username = scanner.nextLine();
        out.print("Inserisci Password: ");
        String password = scanner.nextLine();

        try {
            var session = GraphicAPI.loginApi(new LoginBean(username, password));
            context.setCurrentSession(session);
            out.println("Login effettuato! Benvenuto, " + session.getUser());

            // 🔄 Cambia pagina passando il contesto estratto internamente
            return new CatalogScreen(context);
        } catch (AuthenticationException e) {
            out.println(">> ERRORE: " + e.getMessage());
            return this; // Resta qui
        }
    }
}