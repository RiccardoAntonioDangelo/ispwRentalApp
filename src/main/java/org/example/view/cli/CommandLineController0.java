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
public class CommandLineController0 {

    private final Scanner scanner;
    // 💡 Risolto: Inserito l'output stream configurabile come attributo della classe
    private final PrintStream out;
    private SessionBean currentSession;

    /**
     * Costruttore standard che ripiega sul System.out di default.
     */
    public  CommandLineController0() {
        this(System.out);
    }

    /**
     * Costruttore flessibile per iniettare un qualsiasi flusso di output (es. per testing o log).
     */
    public CommandLineController0(PrintStream outStream) {
        this.scanner = new Scanner(System.in);
        this.out = outStream;
    }

    public void start() {
        printHeader();
        handleCliLogin();

        CatalogBean catalog = GraphicAPI.getCatalog();
        displayCatalog(catalog);

        ProductBean selectedProduct = handleProductSelection(catalog);
        if (selectedProduct == null) return;

        handleRentalRequest(selectedProduct);
    }

    private void handleCliLogin() {
        while (currentSession == null) {
            printLoginPrompt();
            out.print("Inserisci Username: ");
            String username = scanner.nextLine();
            out.print("Inserisci Password: ");
            String password = scanner.nextLine();

            LoginBean loginBean = new LoginBean(username, password);

            try {
                currentSession = GraphicAPI.loginApi(loginBean);
                printLoginSuccess(currentSession.getUser());
            } catch (AuthenticationException e) {
                printError(e.getMessage());
            }
        }
    }

    private ProductBean handleProductSelection(CatalogBean catalog) {
        out.print("\nSeleziona l'indice dell'articolo per vederne i dettagli: ");
        int index = Integer.parseInt(scanner.nextLine());

        if (index < 0 || index >= catalog.getProducts().size()) {
            printMessage("Indice non valido.");
            return null;
        }

        ProductBean product = catalog.getProducts().get(index);
        displayProductDetails(product);

        if (!product.getProduct().isAvailable()) {
            printError(StrAppSystem.get(StrAppSystem.ERR_ITEM_UNAVAILABLE));
            return null;
        }

        return product;
    }

    private void handleRentalRequest(ProductBean product) {
        out.print("\nVuoi procedere con la richiesta di noleggio? (s/n): ");
        String confirm = scanner.nextLine();

        if (!confirm.equalsIgnoreCase("s")) {
            printMessage("Operazione annullata.");
            return;
        }

        printFormHeader();
        out.print("Inserisci Periodo di Noleggio (es. 3 giorni): ");
        String periodo = scanner.nextLine();
        out.print("Inserisci Luogo di ritiro/consegna: ");
        String luogo = scanner.nextLine();

        RentalFormBean rentalForm = new RentalFormBean();

        try {
            GraphicAPI.sendApi(currentSession, rentalForm);
            printRequestSentSuccess();
            simulateOwnerDecision(rentalForm);
        } catch (RentalException e) {
            printError("Durante l'invio della richiesta: " + e.getMessage());
        }
    }

    private void simulateOwnerDecision(RentalFormBean rentalForm) throws RentalException {
        out.print("\n[SIMULAZIONE PROPRIETARIO] Accetti il noleggio? (s/n): ");
        String scelta = scanner.nextLine();

        if (scelta.equalsIgnoreCase("s")) {
            printNotification(StrAppSystem.get(StrAppSystem.MSG_CONFIRM_ACCEPTED));

            out.print("\nEffettuare il pagamento ora? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                GraphicAPI.payRentalApi(currentSession, rentalForm);
                printMessage("Pagamento completato. Il noleggio è stato salvato nella tua area personale.");
            }
        } else {
            printNotification(StrAppSystem.get(StrAppSystem.MSG_CONFIRM_REJECTED));
        }
    }

    // --- Helper di stampa interna che utilizzano l'attributo "out" ---

    private void printHeader() {
        out.println("=== SISTEMA DI NOLEGGIO ARTICOLI ===");
    }

    private void printLoginPrompt() {
        out.println("\n--- Schermata di Login ---");
    }

    private void printLoginSuccess(String user) {
        out.println("Login effettuato con successo! Benvenuto, " + user);
    }

    private void printFormHeader() {
        out.println("\n--- Compilazione MODULO di Noleggio ---");
    }

    private void printRequestSentSuccess() {
        out.println("\nRichiesta inviata con successo al proprietario.");
        out.println("In attesa di approvazione...");
    }

    private void displayCatalog(CatalogBean catalog) {
        out.println("\n--- Articoli Disponibili ---");
        List<ProductBean> products = catalog.getProducts();
        for (int i = 0; i < products.size(); i++) {
            ProductBean p = products.get(i);
            String status = p.getProduct().isAvailable() ? "Disponibile" : "NON Disponibile";
            out.printf("[%d] %s - %s\n", i, p.getProduct().getName(), status);
        }
    }

    private void displayProductDetails(ProductBean product) {
        out.println("\n--- Dettagli Articolo ---");
        out.println("Nome: " + product.getProduct().getName());
        out.println("Prezzo Giornaliero: " + product.getProduct().getPrice());
        out.println("Descrizione: " + product.getProduct().getDescription());
    }

    private void printMessage(String msg) {
        out.println(msg);
    }

    private void printNotification(String msg) {
        out.println(">> NOTIFICA CLIENTE: " + msg);
    }

    private void printError(String error) {
        // Mantenuto su err per mantenere la colorazione rossa nativa dei log di errore
        System.err.println(">> ERRORE: " + error);
    }
}