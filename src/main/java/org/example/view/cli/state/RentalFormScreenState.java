package org.example.view.cli.state;

import org.example.controller.bean.ProductBean;
import org.example.controller.bean.RentalFormBean;
import org.example.exceptions.RentalException;
import org.example.view.GraphicAPI;
import org.example.view.cli.context.CliContext;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class RentalFormScreenState extends AbstractCliScreen {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    // Contatore per gestire l'uscita forzata in caso di troppe 'B' consecutive/complessive
    private int backCount = 0;
    private static final int MAX_BACK_ATTEMPTS = 3;

    // Campi dati interni (esclusa la nota, come da specifiche)
    private String phone = "";
    private String email = "";
    private String luogo = "";
    private long giorni = 0;

    public RentalFormScreenState(CliContext context) {
        super(context);
    }

    @Override
    public CliScreenState handleAction() {
        PrintStream out = context.getOut();
        ProductBean product = context.getSelectedProduct();

        if (product == null) {
            out.println(">> ERRORE: Nessun articolo selezionato.");
            return new CatalogScreen(context);
        }

        RentalFormBean rentalForm = new RentalFormBean().prePopulate(context.getCurrentSession());

        // Pre-popolamento iniziale
        this.phone = rentalForm.getPhone();
        this.email = rentalForm.getEmail();

        out.println("\n--- Mostra MODULO di Noleggio ---");
        out.println("(Digita 'B' in qualsiasi momento per tornare al campo precedente o uscire)\n");

        int step = 1;
        while (step <= 5) {
            step = processStep(step, rentalForm);
            if (step == 0) {
                return new CatalogScreen(context);
            }
        }

        // Configurazione formale prima dell'esecuzione dell'API di invio
        rentalForm.setPhone(this.phone);
        rentalForm.setEmail(this.email);
        rentalForm.setPickupLocation(this.luogo);
        rentalForm.setProductId(product.getProduct().getId());
        rentalForm.setOwnerEmail(product.getProduct().getOwnerEmail());

        if (!rentalForm.isValid()) {
            out.println(">> ERRORE DI VALIDAZIONE: " + rentalForm.getErrorMessage());
            return this;
        }

        try {
            // Esecuzione dell'invio effettivo del form al backend
            GraphicAPI.sendApi(context.getCurrentSession(), rentalForm);
            out.println("\n>> Richiesta inoltrata con successo! In attesa di approvazione.");

            // Ritorna direttamente alla Home come richiesto
            out.println("Ritorno alla schermata principale...");
            return new HomeScreenState(context);

        } catch (RentalException e) {
            out.println(">> ERRORE DURANTE L'INVIO: " + e.getMessage());
            return new CatalogScreen(context);
        }
    }

    /**
     * Gestore dei singoli step privati estratti
     */
    private int processStep(int step, RentalFormBean rentalForm) {
        if (backCount >= MAX_BACK_ATTEMPTS) {
            context.getOut().println("\n>> Hai premuto 'B' troppe volte. Operazione interrotta dal sistema.");
            return 0;
        }

        ProductBean product = context.getSelectedProduct();
        double costoServizio = product.getProduct().getPrice() * this.giorni;

        return switch (step) {
            case 1 -> stepPhone() ? 2 : 1;
            case 2 -> stepEmail() ? 3 : 1;
            case 3 -> stepLuogo() ? 4 : 2;
            case 4 -> stepDate(rentalForm) ? 5 : 3;
            case 5 -> stepConfermaFinal(rentalForm, product, costoServizio);
            default -> step;
        };
    }

    private boolean stepPhone() {
        context.getOut().print("[1/5] Numero di Telefono (Attuale: " + this.phone + "): ");
        String input = context.getScanner().nextLine();
        if (input.equalsIgnoreCase("B")) {
            backCount++;
            context.getOut().printf("Sei già al primo campo. (Uscita per troppe B: %d/%d)", backCount, MAX_BACK_ATTEMPTS);
            return false;
        }
        if (!input.trim().isEmpty()) this.phone = input;
        return true;
    }

    private boolean stepEmail() {
        context.getOut().print("[2/5] Email (Attuale: " + this.email + "): ");
        String input = context.getScanner().nextLine();
        if (input.equalsIgnoreCase("B")) { backCount++; return false; }
        if (!input.trim().isEmpty()) this.email = input;
        return true;
    }

    private boolean stepLuogo() {
        context.getOut().print("[3/5] Luogo di ritiro/consegna: ");
        String input = context.getScanner().nextLine();
        if (input.equalsIgnoreCase("B")) { backCount++; return false; }
        this.luogo = input;
        return true;
    }

    private boolean stepDate(RentalFormBean rentalForm) {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();

        out.println("\n[4/5] Seleziona la modalità di inserimento date:");
        out.println("1) Calcola da oggi (Inizio tra X giorni + Durata N giorni)");
        out.println("2) Inserisci date precise (formato GG/MM/AA)");
        out.print("Scelta (o 'B'): ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("B")) { backCount++; return false; }

        boolean success = false;
        if (input.equals("1")) {
            success = gestisciDateRelative(rentalForm);
        } else if (input.equals("2")) {
            success = gestisciDateAssolute(rentalForm);
        } else {
            out.println("Opzione non valida.");
        }

        if (success) {
            this.giorni = ChronoUnit.DAYS.between(rentalForm.getStart(), rentalForm.getEnd());
        }
        return success;
    }

    /**
     * Interfaccia speculare dell'invio in formato strutturato richiesta dell'utente
     */
    private int stepConfermaFinal(RentalFormBean rentalForm, ProductBean product, double costoServizio) {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();
        String articoloNome = product.getProduct().getName();

        out.println("\n>>> RIEPILOGO RICHIESTA INVIATA <<<");
        out.println("PRODUCT_ID:" + product.getProduct().getOwnerEmail() + "I" + articoloNome);
        out.println("START_DATE:" + rentalForm.getStart());
        out.println("END_DATE:" + rentalForm.getEnd());
        out.println("NAME:" + (rentalForm.getName() != null && !rentalForm.getName().isEmpty() ? rentalForm.getName() : "N/A"));
        out.println("SURNAME:" + (rentalForm.getSurname() != null && !rentalForm.getSurname().isEmpty() ? rentalForm.getSurname() : "N/A"));
        out.println("EMAIL:" + this.email);
        out.println("PHONE:" + this.phone);
        out.println("PICKUP:" + this.luogo);
        out.println("STATUS:PENDING");
        out.println("Periodo di Noleggio Calcolato: " + this.giorni + " giorni");
        out.println("Articolo Richiesto: " + articoloNome);
        out.println("Costo del Servizio: €" + costoServizio);
        out.println("----------------------------------------");

        out.print("Confermi l'invio della richiesta? (s/n/B per modificare): ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("B")) { backCount++; return 4; }
        if (input.equalsIgnoreCase("s")) return 6; // Termina il loop con successo ed esegue sendApi

        return 0; // Esci e annulla (ritorna al catalogo)
    }

    private boolean gestisciDateRelative(RentalFormBean rentalForm) {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();
        try {
            out.print("   > Tra quanti giorni vuoi iniziare il noleggio? (0 = oggi): ");
            String inizioIn = scanner.nextLine();
            if (inizioIn.equalsIgnoreCase("B")) { backCount++; return false; }
            long giorniAInizio = Long.parseLong(inizioIn);

            out.print("   > Quanti giorni durerà il noleggio? (minimo 1): ");
            String durataIn = scanner.nextLine();
            if (durataIn.equalsIgnoreCase("B")) { backCount++; return false; }
            long durata = Long.parseLong(durataIn);

            LocalDate dataInizio = LocalDate.now(ZoneId.systemDefault()).plusDays(giorniAInizio);
            rentalForm.setStart(dataInizio);
            rentalForm.setEnd(dataInizio.plusDays(durata));
            return true;
        } catch (NumberFormatException e) {
            out.println("   >> Errore: Inserisci un numero valido.");
            return false;
        }
    }

    private boolean gestisciDateAssolute(RentalFormBean rentalForm) {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();
        try {
            out.print("   > Inserisci Data Inizio (GG/MM/AA): ");
            String inizioIn = scanner.nextLine();
            if (inizioIn.equalsIgnoreCase("B")) { backCount++; return false; }
            LocalDate dataInizio = LocalDate.parse(inizioIn, DATE_FORMATTER);

            out.print("   > Inserisci Data Fine (GG/MM/AA): ");
            String fineIn = scanner.nextLine();
            if (fineIn.equalsIgnoreCase("B")) { backCount++; return false; }
            LocalDate dataFine = LocalDate.parse(fineIn, DATE_FORMATTER);

            rentalForm.setStart(dataInizio);
            rentalForm.setEnd(dataFine);
            return true;
        } catch (DateTimeParseException e) {
            out.println("   >> Errore: Formato data errato. Usa il formato corretto (es. 25/12/26).");
            return false;
        }
    }
}