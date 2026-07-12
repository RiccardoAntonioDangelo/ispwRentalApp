package org.example.view.cli;

import org.example.controller.bean.*;
import org.example.exceptions.AuthenticationException;
import org.example.exceptions.RentalException;
import org.example.util.str.StrAppSystem;
import org.example.view.GraphicAPI;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Controller per la gestione dell'interfaccia a riga di comando (CLI).
 */
public class CommandLineController {

    private final Scanner scanner;
    private final PrintStream out; // 🆕 Raccolto come attributo per l'output standard
    private final PrintStream err; // 🆕 Raccolto come attributo per l'output di errore
    private SessionBean currentSession;

    /**
     * Costruttore di default che punta alla console standard del sistema.
     */
    public CommandLineController() {
        this(System.out, System.err);
    }

    /**
     * Costruttore parametrizzato utile per iniettare stream personalizzati (es. nei Test).
     */
    public CommandLineController(PrintStream out, PrintStream err) {
        this.scanner = new Scanner(System.in);
        this.out = out;
        this.err = err;
    }

    /**
     * Avvia il flusso principale del programma CLI.
     */
    public void start() {
        out.println("=== SISTEMA DI NOLEGGIO ARTICOLI ===");
        
        handleCliLogin();

        boolean running = true;
        while (running) {
            out.println("\n===== MENU PRINCIPALE =====");
            out.println("[1] Visualizza Catalogo e Noleggia");
            out.println("[2] Gestisci le tue Richieste di Noleggio (Area Personale)");
            out.println("[3] Effettua il Logout ed Esci");
            out.print("Scegli un'opzione: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleBrowseAndRent();
                case "2" -> handleUserRentalsArea();
                case "3" -> {
                    GraphicAPI.logoutApi(currentSession);
                    out.println("Logout effettuato. Arrivederci!");
                    running = false;
                }
                default -> out.println("Opzione non valida. Riprova.");
            }
        }
    }

    private void handleCliLogin() {
        while (currentSession == null) {
            out.println("\n--- Schermata di Login ---");
            out.print("Inserisci Username: ");
            String username = scanner.nextLine();
            out.print("Inserisci Password: ");
            String password = scanner.nextLine();

            LoginBean loginBean = new LoginBean(username, password);

            try {
                currentSession = GraphicAPI.loginApi(loginBean);
                out.println("Login effettuato con successo! Benvenuto, " + currentSession.getUser());
            } catch (AuthenticationException e) {
                err.println(">> ERRORE AUTENTICAZIONE: " + e.getMessage());
            }
        }
    }

    private void handleBrowseAndRent() {
        CatalogBean catalog = GraphicAPI.getCatalog();
        displayCatalog(catalog);

        ProductBean selectedProduct = handleProductSelection(catalog);
        if (selectedProduct != null) {
            handleRentalRequest(selectedProduct);
        }
    }

    private void displayCatalog(CatalogBean catalog) {
        out.println("\n--- Articoli Disponibili ---");
        List<ProductBean> products = catalog.getProducts();
        for (int i = 0; i < products.size(); i++) {
            ProductBean p = products.get(i);
            String status = p.getProduct().isAvailable() ? "Disponibile" : "NON Disponibile";
            out.printf("[%d] %s (Categoria: %s) - Prezzo: %.2f€/giorno [%s]\n", 
                    i, p.getProduct().getName(), p.getProduct().getDescription(), p.getProduct().getPrice(), status);
        }
    }

    private ProductBean handleProductSelection(CatalogBean catalog) {
        out.print("\nSeleziona l'indice dell'articolo per vederne i dettagli (-1 per tornare indietro): ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index == -1) return null;

            if (index < 0 || index >= catalog.getProducts().size()) {
                out.println("Indice non valido.");
                return null;
            }

            ProductBean product = catalog.getProducts().get(index);
            
            out.println("\n--- Dettagli Articolo ---");
            out.println("Nome: " + product.getProduct().getName());
            out.println("Prezzo Giornaliero: " + product.getProduct().getPrice() + "€");
            out.println("Descrizione: " + product.getProduct().getDescription());
            
            if (!product.getProduct().isAvailable()) {
                err.println(">> ATTENZIONE: " + StrAppSystem.get(StrAppSystem.ERR_ITEM_UNAVAILABLE));
                return null;
            }
            return product;
        } catch (NumberFormatException e) {
            out.println("Inserimento non valido. Digita un numero.");
            return null;
        }
    }

    private void handleRentalRequest(ProductBean product) {
        out.print("\nVuoi procedere con la richiesta di noleggio per questo articolo? (s/n): ");
        if (!scanner.nextLine().equalsIgnoreCase("s")) {
            out.println("Operazione annullata.");
            return;
        }

        out.println("\n--- Compilazione MODULO di Noleggio ---");
        out.print("Inserisci il tuo Telefono: ");
        String phone = scanner.nextLine();
        out.print("Inserisci Luogo di ritiro/consegna: ");
        String locate = scanner.nextLine();

        RentalFormBean rentalForm = new RentalFormBean();
        rentalForm.setEmail(currentSession.getUser());
        rentalForm.setPhone(phone);
        rentalForm.setPickupLocation(locate);
        rentalForm.setProductId(product.getProduct().getId());
        rentalForm.setOwnerEmail(product.getProduct().getOwnerEmail());

        try {
            GraphicAPI.sendApi(currentSession, rentalForm);
            out.println("\nRichiesta inviata con successo al proprietario dell'articolo!");
            out.println("Puoi monitorare lo stato nell'Area Personale.");
        } catch (RentalException e) {
            err.println(">> Errore durante l'invio della richiesta: " + e.getMessage());
        }
    }

    private void handleUserRentalsArea() {
        out.println("\n--- Area Personale: Coda e Storico Richieste ---");
        List<RentalFormBean> userRentals = GraphicAPI.getUserRentals(currentSession);

        if (userRentals.isEmpty()) {
            out.println("Non ci sono pratiche di noleggio registrate a tuo nome o associate ai tuoi prodotti.");
            return;
        }

        for (int i = 0; i < userRentals.size(); i++) {
            RentalFormBean rental = userRentals.get(i);
            out.printf("[%d] Prodotto ID: %s | Cliente: %s | Proprietario: %s | Stato: %s\n",
                    i, rental.getProductId(), rental.getEmail(), rental.getOwnerEmail(), rental.getOwnerEmail());//todo
        }

        out.print("\nSeleziona l'indice della pratica per effettuare un'azione (-1 per uscire): ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index == -1) return;

            if (index < 0 || index >= userRentals.size()) {
                out.println("Indice non valido.");
                return;
            }

            RentalFormBean selectedRental = userRentals.get(index);
            handleRentalActions(selectedRental);

        } catch (NumberFormatException e) {
            out.println("Input non valido.");
        }
    }

    private void handleRentalActions(RentalFormBean rental) {
        out.println("\n--- Gestione Pratica Selezionata ---");
        out.println("[1] Paga e Finalizza Noleggio (Solo se APPROVED dal proprietario)");
        out.println("[2] Accetta Noleggio (Solo se sei il Proprietario dell'articolo)");
        out.println("[3] Rifiuta Noleggio (Solo se sei il Proprietario dell'articolo)");
        out.println("[4] Annulla / Rimuovi Pratica");
        out.println("[5] Torna al menu");
        out.print("Seleziona l'azione da eseguire: ");

        String action = scanner.nextLine();
        try {
            switch (action) {
                case "1" -> {
                    GraphicAPI.payRentalApi(currentSession, rental);
                    out.println("Pagamento completato con successo!");
                }
                case "2" -> {
                    GraphicAPI.acceptRentalApi(currentSession, rental);
                    out.println(">> NOTIFICA: " + StrAppSystem.get(StrAppSystem.MSG_CONFIRM_ACCEPTED));
                }
                case "3" -> {
                    GraphicAPI.rejectRentalApi(currentSession, rental);
                    out.println(">> NOTIFICA: " + StrAppSystem.get(StrAppSystem.MSG_CONFIRM_REJECTED));
                }
                case "4" -> {
                    GraphicAPI.cancelRentalApi(currentSession, rental);
                    out.println("Pratica annullata/rimossa con successo.");
                }
                default -> out.println("Azione annullata o non riconosciuta.");
            }
        } catch (RentalException e) {
            err.println(">> OPERAZIONE FALLITA: " + e.getMessage());
        }
    }
}