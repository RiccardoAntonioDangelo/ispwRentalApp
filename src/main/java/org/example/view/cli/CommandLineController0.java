package org.example.view.cli;

import org.example.controller.bean.*;
import org.example.exceptions.AuthenticationException;
import org.example.exceptions.RentalException;
import org.example.util.str.StrAppSystem;
import org.example.view.GraphicAPI;

import java.util.List;
import java.util.Scanner;

/**
 * Controller per la gestione dell'interfaccia a riga di comando (CLI).
 * Riproduce fedelmente i passi interni dello Use Case "Noleggia articolo".
 */
public class CommandLineController0 {

    private final Scanner scanner;
    private SessionBean currentSession;

    public CommandLineController0() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Avvia il flusso principale del programma CLI.
     */
    public void start() {
        System.out.println("=== SISTEMA DI NOLEGGIO ARTICOLI ===");
        
        // 1. Forza il login iniziale (Passo 2 e 3 dello Use Case)
        handleCliLogin();

        // 2. Mostra gli articoli disponibili dopo il login con successo (Passo 4)
        CatalogBean catalog = GraphicAPI.getCatalog();
        displayCatalog(catalog);

        // 3. Selezione dell'articolo (Passo 5 e 6)
        ProductBean selectedProduct = handleProductSelection(catalog);
        if (selectedProduct == null) return;

        // 4. Modulo di noleggio e invio richiesta (Passo 7, 8, 9, 10)
        handleRentalRequest(selectedProduct);
    }

    /**
     * Gestisce il sotto-flusso di login via terminale.
     * [Internal Step 3a]: Se le credenziali falliscono, mostra l'errore e resta nel loop di login.
     */
    private void handleCliLogin() {
        while (currentSession == null) {
            System.out.println("\n--- Schermata di Login ---");
            System.out.print("Inserisci Username: ");
            String username = scanner.nextLine();
            System.out.print("Inserisci Password: ");
            String password = scanner.nextLine();

            LoginBean loginBean = new LoginBean(username, password);

            try {
                // Invocazione della Facade comune
                currentSession = GraphicAPI.loginApi(loginBean);
                System.out.println("Login effettuato con successo! Benvenuto, " + currentSession.getUser());
            } catch (AuthenticationException e) {
                // Intercetta l'eccezione configurata con StrAppSystem.ERR_AUTH_FAILED
                System.err.println(">> ERRORE: " + e.getMessage());
            }
        }
    }

    /**
     * Stampa a schermo i prodotti recuperati dal catalogo di business.
     */
    private void displayCatalog(CatalogBean catalog) {
        System.out.println("\n--- Articoli Disponibili ---");
        List<ProductBean> products = catalog.getProducts();
        for (int i = 0; i < products.size(); i++) {
            ProductBean p = products.get(i);
            String status = p.getProduct().isAvailable() ? "Disponibile" : "NON Disponibile";
            System.out.printf("[%d] %s - %s\n", i, p.getProduct().getName(), status);
        }
    }

    /**
     * Gestisce la selezione dell'indice dell'articolo da terminale.
     * [Internal Step 10a]: Verifica se l'articolo è disponibile prima di procedere.
     */
    private ProductBean handleProductSelection(CatalogBean catalog) {
        System.out.print("\nSeleziona l'indice dell'articolo per vederne i dettagli: ");
        int index = Integer.parseInt(scanner.nextLine());

        if (index < 0 || index >= catalog.getProducts().size()) {
            System.out.println("Indice non valido.");
            return null;
        }

        ProductBean product = catalog.getProducts().get(index);
        
        System.out.println("\n--- Dettagli Articolo ---");
        System.out.println("Nome: " + product.getProduct().getName());
        System.out.println("Prezzo Giornaliero: " + product.getProduct().getPrice());
        System.out.println("Descrizione: " + product.getProduct().getDescription());

        // Controllo Step 10a
        if (!product.getProduct().isAvailable()) {
            System.err.println(">> ERRORE: " + StrAppSystem.get(StrAppSystem.ERR_ITEM_UNAVAILABLE));
            return null;
        }

        return product;
    }

    /**
     * Compilazione del MODULO di noleggio (Storyboard 2.5) via CLI e invio della richiesta.
     */
    private void handleRentalRequest(ProductBean product) {
        System.out.print("\nVuoi procedere con la richiesta di noleggio? (s/n): ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("s")) {
            System.out.println("Operazione annullata.");
            return;
        }

        System.out.println("\n--- Compilazione MODULO di Noleggio ---");
        System.out.print("Inserisci Periodo di Noleggio (es. 3 giorni): ");
        String periodo = scanner.nextLine();
        System.out.print("Inserisci Luogo di ritiro/consegna: ");
        String luogo = scanner.nextLine();

        // Costruzione del modulo bean (nel tuo scenario reale valorizzerai i campi interni o userai un factory/getRentalForm)
        RentalFormBean rentalForm = new RentalFormBean();
        // setup fittizio dei dettagli inseriti dall'utente per la richiesta

        try {
            // Invio della richiesta (Il sistema notifica il proprietario con i dati di sessione e modulo)
            GraphicAPI.sendApi(currentSession, rentalForm);
            System.out.println("\nRichiesta inviata con successo al proprietario.");
            System.out.println("In attesa di approvazione...");
            
            // Simulazione del flusso di approvazione/accettazione del proprietario (Passo 11 e 12)
            // In un sistema CLI sincrono o dummy possiamo simulare direttamente l'accettazione/pagamento
            simulateOwnerDecision(rentalForm);

        } catch (RentalException e) {
            System.err.println(">> Errore durante l'invio della richiesta: " + e.getMessage());
        }
    }

    /**
     * Simula la decisione del proprietario ed il successivo pagamento del cliente (Passi finali).
     */
    private void simulateOwnerDecision(RentalFormBean rentalForm) throws RentalException {
        System.out.print("\n[SIMULAZIONE PROPRIETARIO] Accetti il noleggio? (s/n): ");
        String scelta = scanner.nextLine();

        if (scelta.equalsIgnoreCase("s")) {
            // Passo 12: Messaggio di conferma
            System.out.println(">> NOTIFICA CLIENTE: " + StrAppSystem.get(StrAppSystem.MSG_CONFIRM_ACCEPTED));
            
            // Passo 13 e 14: Pagamento e finalizzazione
            System.out.print("\nEffettuare il pagamento ora? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                GraphicAPI.payRentalApi(currentSession, rentalForm);
                System.out.println("Pagamento completato. Il noleggio è stato salvato nella tua area personale.");
            }
        } else {
            // [Internal Step 11a]: Rifiuto
            System.out.println(">> NOTIFICA CLIENTE: " + StrAppSystem.get(StrAppSystem.MSG_CONFIRM_REJECTED));
        }
    }
}