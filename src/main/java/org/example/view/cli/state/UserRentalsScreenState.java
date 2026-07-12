package org.example.view.cli.state;

import org.example.controller.bean.RentalFormBean;
import org.example.exceptions.RentalException;
import org.example.util.str.StrApp;
import org.example.view.GraphicAPI;
import org.example.view.cli.context.CliContext;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class UserRentalsScreenState extends AbstractCliScreen {

    public UserRentalsScreenState(CliContext context) {
        super(context);
    }

    @Override
    public CliScreenState handleAction() {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();

        out.println("\n=== " + StrApp.NAV_USER_RENTAL + " ===");

        // Controllo della sessione analogo a JavaFX (this.memory())
        if (context.getCurrentSession() == null) {
            out.println(">> ERRORE: " + StrApp.ERR_INVALID_SESSION);
            out.println("Premi INVIO per tornare al login...");
            scanner.nextLine();
            return new LoginScreen(context);
        }

        // Recupero dei noleggi tramite le API comuni
        List<RentalFormBean> rentals = GraphicAPI.getUserRentals(context.getCurrentSession());

        if (rentals == null || rentals.isEmpty()) {
            out.println("\n" + StrApp.RENTALS_EMPTY_MESSAGE);
            out.println("\nPremi INVIO per tornare al catalogo principale...");
            scanner.nextLine();
            return new CatalogScreen(context);
        }

        // Mostra l'elenco dei noleggi stile "lista di card"
        out.println("\nEcco i tuoi noleggi attivi e passati:");
        out.println("----------------------------------------");
        for (int i = 0; i < rentals.size(); i++) {
            RentalFormBean r = rentals.get(i);
            
            // Estrae lo stato (PENDING, ACCEPTED, PAID, REJECTED)
            String status = r.getRental() != null ? r.getRental().getStatus().toString() : "N/A";
            
            out.printf("[%d] Articolo ID: %s | Periodo: %s -> %s | STATO: %s",
                    i, r.getProductId(), r.getStart(), r.getEnd(), status);
        }
        out.println("----------------------------------------");

        out.print("Seleziona l'indice di un noleggio per gestirlo (-1 per tornare al catalogo): ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("B") || input.equals("-1")) {
            return new CatalogScreen(context);
        }

        try {
            int index = Integer.parseInt(input);
            if (index < 0 || index >= rentals.size()) {
                out.println("Indice non valido.");
                return this; // Ricarica la schermata attuale
            }

            RentalFormBean selectedRental = rentals.get(index);
            return handleRentalInteraction(selectedRental);

        } catch (NumberFormatException e) {
            out.println("Inserimento non valido. Digita un numero.");
            return this;
        }
    }

    /**
     * Gestisce l'interazione con il singolo noleggio selezionato (Simula l'azione della Card)
     */
    private CliScreenState handleRentalInteraction(RentalFormBean rental) {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();

        out.println("\n--- DETTAGLI NOLEGGIO SELEZIONATO ---");
        out.println("PRODUCT_ID:" + rental.getProductId());
        out.println("START_DATE:" + rental.getStart());
        out.println("END_DATE:" + rental.getEnd());
        out.println("EMAIL:" + rental.getEmail());
        out.println("PHONE:" + rental.getPhone());
        out.println("PICKUP:" + rental.getPickupLocation());
        
        String status = rental.getRental() != null ? rental.getRental().getStatus().toString() : "PENDING";
        out.println("STATUS:" + status);
        out.println("-------------------------------------");

        // Se il noleggio è stato ACCETTATO dal proprietario ma non ancora pagato, offre l'azione di pagamento
        if ("ACCEPTED".equalsIgnoreCase(status)) {
            out.print("Questo noleggio è stato accettato! Vuoi procedere al pagamento ora? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                try {
                    GraphicAPI.payRentalApi(context.getCurrentSession(), rental);
                    out.println(">> Pagamento completato con successo!");
                } catch (RentalException e) {
                    out.println(">> ERRORE DURANTE IL PAGAMENTO: " + e.getMessage());
                }
            }
        } else if ("PAID".equalsIgnoreCase(status)) {
            out.println("Questo noleggio è già stato pagato e confermato.");
            out.println("Premi INVIO per tornare alla lista...");
            scanner.nextLine();
        } else {
            out.println("In attesa di risposta da parte del proprietario o rifiutato.");
            out.println("Premi INVIO per tornare alla lista...");
            scanner.nextLine();
        }

        return this; // Ritorna alla lista aggiornata dei noleggi (effetto updateView())
    }
}