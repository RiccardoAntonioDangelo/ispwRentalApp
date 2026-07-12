package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.view.cli.context.CommandLineController;
import org.example.view.javafx.main.MainShellContext;

import java.io.PrintStream;
import java.util.Scanner;

public class Main extends Application {

    private static EnumDaoType chosenDaoType = EnumDaoType.FILE;

    public static void main(String[] args) {
        // Assegnazione degli stream standard a variabili dedicate
        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;

        out.println("==================================================");
        out.println("       CONFIGURAZIONE AVVIO APPLICAZIONE          ");
        out.println("==================================================");

        // 1. SCELTA DELLA PERSISTENZA
        out.println("\n[1] Seleziona il tipo di Persistenza (DAO):");
        out.println("1) DEMO (Dati in memoria / Mock)");
        out.println("2) FILE (JSON / File locali)");
        out.println("3) DBMS (Database relazionale)");
        out.print("Scelta: ");
        String dbChoice = scanner.nextLine().trim();

        switch (dbChoice) {
            case "1":
                chosenDaoType = EnumDaoType.DEMO;
                break;
            case "3":
                chosenDaoType = EnumDaoType.DBMS;
                break;
            case "2":
            default:
                chosenDaoType = EnumDaoType.FILE;
                break;
        }

        // 2. SCELTA DELL'INTERFACCIA UTENTE
        out.println("\n[2] Seleziona l'interfaccia utente da attivare:");
        out.println("1) CLI (Interfaccia a riga di comando)");
        out.println("2) JavaFX (Interfaccia Grafica)");
        out.print("Scelta: ");
        String viewChoice = scanner.nextLine().trim();

        out.println("==================================================\n");

        if (viewChoice.equals("1")) {
            // Inizializzazione DAO e avvio CLI passando la variabile dello stream di output
            DAOManager.initializeSingleton(chosenDaoType, true, true);

            CommandLineController cliController = new CommandLineController(out);
            cliController.start();
        } else {
            // Avvio modalità grafica
            launch(args);
        }
    }

    @Override
    public void start(Stage stage) {
        DAOManager.initializeSingleton(chosenDaoType, true, true);
        new MainShellContext(stage).show();
    }
}