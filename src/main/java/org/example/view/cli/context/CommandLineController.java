package org.example.view.cli.context;

import org.example.view.cli.state.CliScreenState;
import org.example.view.cli.state.LoginScreen;
import org.example.view.cli.state.HomeScreenState; // O CatalogScreen in base a dove vuoi reindirizzare

import java.io.PrintStream;

public class CommandLineController extends CliContext {

    private CliScreenState currentScreen;

    public CommandLineController(PrintStream outStream) {
        super(outStream);
        // Utilizza direttamente 'this' come contesto poiché CommandLineController estende CliContext
        this.currentScreen = new HomeScreenState(this);
    }

    public void start() {
        while (currentScreen != null) {
            try {
                // Esecuzione standard dello stato corrente
                currentScreen = currentScreen.handleAction();
            } catch (Exception e) {
                // Intercettazione centralizzata di qualsiasi eccezione a runtime
                handleGlobalError(e);
            }
        }
        this.getOut().println("\n>> Applicazione CLI terminata in sicurezza.");
    }

    /**
     * Funzione centralizzata per la gestione e il recupero dagli errori.
     * Impedisce la chiusura dell'app e riporta l'utente a uno stato sicuro.
     */
    @Override
    public void handleGlobalError(Exception e) {
        PrintStream out = this.getOut();

        out.println("\n==================================================");
        out.println(">> ERRORE DI SISTEMA RILEVATO");
        out.println(">> Dettaglio: " + (e.getMessage() != null ? e.getMessage() : "Errore sconosciuto."));
        out.println("==================================================");
        out.println("Tentativo di ripristino della sessione in corso... Premi INVIO.");
        this.getScanner().nextLine();

        // Fallback Strategy: rimanda l'utente a una schermata sicura invece di chiudere l'app
        if (this.getCurrentSession() != null) {
            this.currentScreen = new HomeScreenState(this);
        } else {
            this.currentScreen = new LoginScreen(this);
        }
    }
}