package org.example.view.cli.state;

import org.example.controller.bean.RegisterBean;
import org.example.exceptions.AuthenticationException;
import org.example.util.str.StrApp;
import org.example.view.GraphicAPI;
import org.example.view.cli.context.CliContext;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class RegisterScreen extends AbstractCliScreen {

    // Costanti per mappare lo spacchettamento della matrice dei ruoli come in JavaFX
    private static final int INDEX_ROLE_ID   = 0;
    private static final int INDEX_ROLE_NAME = 1;
    private static final int INDEX_ROLE_DESC = 2;

    private int backCount = 0;
    private static final int MAX_BACK_ATTEMPTS = 3;

    // Variabili di stato interne per l'inserimento dati
    private String selectedRoleId = "";
    private String email = "";
    private String password = "";
    private String confirmPassword = "";

    public RegisterScreen(CliContext context) {
        super(context);
    }

    @Override
    public CliScreenState handleAction() {
        PrintStream out = context.getOut();

        out.println("\n=== " + StrApp.SIGNUP_TITLE + " ===");
        out.println(StrApp.SIGNUP_SUBTITLE);
        out.println("(Digita 'B' in qualsiasi momento per tornare al campo precedente o annullare)\n");

        // Recupero dinamico dei ruoli disponibili (come buildDynamicRoles in JavaFX)
        List<String[]> availableRoles = GraphicAPI.getAllRoles();

        int step = 1;
        while (step <= 4) {
            step = processStep(step, availableRoles);
            if (step == 0) {
                out.println(">> Registrazione annullata. Ritorno alla schermata principale.");
                return new HomeScreenState(context); // Mappa l'effetto del goBack() / handleClose()
            }
        }

        // Creazione del Bean per la validazione dei dati inseriti
        RegisterBean registerData = new RegisterBean(selectedRoleId, this.email, this.password, this.confirmPassword);

        if (!registerData.isValid()) {
            out.println("\n>> " + StrApp.SIGNUP_ERROR + ": " + registerData.getErrorMessage());
            return this; // Ricarica la schermata per permettere il reinserimento pulito
        }

        try {
            // Logica core speculare al controller grafico
            GraphicAPI.logoutApi(context.getCurrentSession());
            context.setCurrentSession(GraphicAPI.registerApi(registerData));

            out.println("\n>> Registrazione completata con successo! Benvenuto.");
            return new HomeScreenState(context); // Va alla home una volta autenticato

        } catch (AuthenticationException e) {
            out.println("\n>> " + StrApp.SIGNUP_ERROR + ": " + e.getMessage());
            return this;
        }
    }

    /**
     * Gestore sequenziale dei singoli step privati estratti
     */
    private int processStep(int step, List<String[]> roles) {
        if (backCount >= MAX_BACK_ATTEMPTS) {
            context.getOut().println("\n>> Hai premuto 'B' troppe volte. Operazione interrotta.");
            return 0;
        }

        return switch (step) {
            case 1 -> stepRole(roles) ? 2 : 1;
            case 2 -> stepEmail() ? 3 : 1;
            case 3 -> stepPassword() ? 4 : 2;
            case 4 -> stepConfirmPassword() ? 5 : 3;
            default -> step;
        };
    }

    private boolean stepRole(List<String[]> roles) {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();

        out.println("[1/4] " + StrApp.SIGNUP_ROLE_LABEL);
        for (int i = 0; i < roles.size(); i++) {
            String[] r = roles.get(i);
            out.printf("  %d) %s - %s ", i + 1, r[INDEX_ROLE_NAME], r[INDEX_ROLE_DESC]);
        }
        out.print("Seleziona il numero del ruolo (o 'B'): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("B")) {
            backCount++;
            return false;
        }

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < roles.size()) {
                this.selectedRoleId = roles.get(index)[INDEX_ROLE_ID];
                return true;
            }
        } catch (NumberFormatException e) {context.handleGlobalError(e);}

        out.println("Scelta non valida. Riprova.");
        return false;
    }

    private boolean stepEmail() {
        context.getOut().print("[2/4] " + StrApp.SIGNUP_EMAIL + ": ");
        String input = context.getScanner().nextLine().trim();
        if (input.equalsIgnoreCase("B")) { backCount++; return false; }
        this.email = input;
        return true;
    }

    private boolean stepPassword() {
        context.getOut().print("[3/4] " + StrApp.SIGNUP_PASSWORD + ": ");
        String input = context.getScanner().nextLine().trim();
        if (input.equalsIgnoreCase("B")) { backCount++; return false; }
        this.password = input;
        return true;
    }

    private boolean stepConfirmPassword() {
        context.getOut().print("[4/4] " + StrApp.SIGNUP_CONFIRMPASSWORD + ": ");
        String input = context.getScanner().nextLine().trim();
        if (input.equalsIgnoreCase("B")) { backCount++; return false; }
        this.confirmPassword = input;
        return true;
    }
}