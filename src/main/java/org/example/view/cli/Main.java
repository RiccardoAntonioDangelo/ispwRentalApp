package org.example.view.cli;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.view.cli.context.CommandLineController;

/**
 * Punto di ingresso principale dell'applicazione.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Carica i dati fittizi nel sistema di persistenza (Prodotti, Utenti, Sessioni)
        DAOManager.initializeSingleton(EnumDaoType.FILE, true, true);

        // 2. Istanzia il controller dell'interfaccia a riga di comando (configurato internamente con lo State Pattern)
        CommandLineController cliController = new CommandLineController(System.out);

        // 3. Avvia il ciclo guidato dagli stati
        cliController.start();
    }
}