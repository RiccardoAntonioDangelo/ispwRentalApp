package org.example.view.cli.state;

import org.example.util.str.StrApp;
import org.example.view.GraphicAPI;
import org.example.view.cli.context.CliContext;

import java.io.PrintStream;
import java.util.Scanner;

public class HomeScreenState extends AbstractCliScreen {

    public HomeScreenState(CliContext context) {
        super(context);
    }

    @Override
    public CliScreenState handleAction() {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();

        // --- HEADER E BENVENUTO ---
        out.println("\n" + StrApp.LOGO_MAIN_EMOJI + " --- " + StrApp.WELCOME_TITLE + " --- " + StrApp.LOGO_MAIN_EMOJI);
        out.println(StrApp.WELCOME_DESC);
        out.println("==================================================");

        // --- SEZIONI CONTENUTO ---
        out.println("\n" + StrApp.LOGO_CARD_RENT_EMOJI + " [ Ruolo: " + StrApp.ROLE_CLIENT + " ] -> " + StrApp.CARD_RENT_TITLE);
        out.println("    " + StrApp.CARD_RENT_DESC);
        out.println("\n" + StrApp.LOGO_CARD_ADD_EMOJI + " [ Ruolo: " + StrApp.ROLE_OWNER + " ] -> " + StrApp.CARD_ADD_TITLE);
        out.println("    " + StrApp.CARD_ADD_DESC);
        out.println("==================================================");

        // --- BARRA DELLE AZIONI GLOBALI ---
        out.println("\nSeleziona un'azione:");
        out.println("1) " + StrApp.BTN_RENT_START + " (Catalogo)");
        out.println("2) " + StrApp.BTN_ADD_START + " (Aggiungi Articolo)");
        out.println("--------------------------------------------------");
        out.println("R) " + StrApp.NAV_REGISTER + " (Registra Nuovo Utente)");
        out.println("N) " + StrApp.NAV_USER_RENTAL + " (I Miei Noleggi)");
        out.println("L) Accedi (Login)");
        out.println("Q) " + StrApp.LOGO_LOGOUT + " (Disconnetti)");
        out.println("E) Esci dall'applicazione"); // <--- NUOVA VOCE DI USCITA
        out.print("Scelta: ");

        String scelta = scanner.nextLine().trim().toUpperCase();

        switch (scelta) {
            case "1":
                return new CatalogScreen(context);

            case "2":
                out.println("\n[INFO] Funzionalità Aggiungi Articolo in arrivo...");
                return this;

            case "R":
                return new RegisterScreen(context);

            case "N":
                return new UserRentalsScreenState(context);

            case "L":
                return new LoginScreen(context);

            case "Q":
                if (context.getCurrentSession() != null) {
                    GraphicAPI.logoutApi(context.getCurrentSession());
                }
                context.setCurrentSession(null);
                out.println(">> Logout effettuato con successo.");
                return new LoginScreen(context);

            case "E":
                // <--- GESTIONE USCITA DEFINITIVA
                if (context.getCurrentSession() != null) {
                    try {
                        GraphicAPI.logoutApi(context.getCurrentSession());
                    } catch (Exception e) {this.getCliContext().handleGlobalError(e);}
                }
                out.println("Chiusura dei moduli in corso...");
                return null; // Ritornando null, il CommandLineController interrompe il thread/loop principale

            default:
                out.println(">> Opzione non valida. Riprova.");
                return this;
        }
    }
}