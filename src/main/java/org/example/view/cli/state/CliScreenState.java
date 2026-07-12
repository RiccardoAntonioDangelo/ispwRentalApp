package org.example.view.cli.state;

import org.example.view.cli.context.CliContext;

public interface CliScreenState {
    /**
     * Esegue la logica interna dello schermo.
     */
    CliScreenState handleAction();

    /**
     * Ritorna il contesto CLI associato a questa schermata.
     */
    CliContext getCliContext();
}