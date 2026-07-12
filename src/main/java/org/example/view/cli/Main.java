package org.example.view.cli;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;

/**
 * Punto di ingresso principale dell'applicazione.
 */
public class Main {
    
    public static void main(String[] args) {
        // 1. Carica i dati fittizi nel sistema di persistenza (Prodotti, Utenti, Sessioni)
        DAOManager.initializeSingleton(EnumDaoType.FILE,true,true);

        // 2. Istanzia il controller dell'interfaccia a riga di comando
        CommandLineController0 cliController = new CommandLineController0();

        // 3. Avvia il ciclo del programma
        cliController.start();
    }
}