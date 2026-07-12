package org.example.view.cli.state;

import org.example.controller.bean.ProductBean;
import org.example.util.str.StrAppSystem;
import org.example.view.cli.context.CliContext;

public class ProductDetailsScreen extends AbstractCliScreen {

    public ProductDetailsScreen(CliContext context) {
        super(context);
    }

    @Override
    public CliScreenState handleAction() {
        ProductBean product = context.getSelectedProduct();
        var out = context.getOut();
        var scanner = context.getScanner();

        out.println("\n--- Schermata Dettagli Articolo ---");
        for (String line : product.getCompleteDetails()) {
            out.println(line);
        }

        if (!product.getProduct().isAvailable()) {
            out.println("\n>> " + StrAppSystem.get(StrAppSystem.ERR_ITEM_UNAVAILABLE));
            out.println("Premi INVIO per tornare al catalogo...");
            scanner.nextLine();
            return new CatalogScreen(context);
        }

        out.print("\nVuoi procedere con la richiesta di noleggio? (s/n): ");
        if (!scanner.nextLine().equalsIgnoreCase("s")) {
            out.println("Operazione annullata. Ritorno al catalogo.");
            return new CatalogScreen(context);
        }

        return new RentalFormScreenState(context);
    }
}