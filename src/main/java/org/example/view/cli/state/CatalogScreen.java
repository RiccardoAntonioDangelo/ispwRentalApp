package org.example.view.cli.state;

import org.example.controller.bean.CatalogBean;
import org.example.controller.bean.ProductBean;
import org.example.view.GraphicAPI;
import org.example.view.cli.context.CliContext;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class CatalogScreen extends AbstractCliScreen {

    public CatalogScreen(CliContext context) {
        super(context);
    }

    @Override
    public CliScreenState handleAction() {
        PrintStream out = context.getOut();
        Scanner scanner = context.getScanner();

        out.println("\n--- Articoli Disponibili ---");
        CatalogBean catalog = GraphicAPI.getCatalog();
        List<ProductBean> products = catalog.getProducts();

        if (products == null || products.isEmpty()) {
            out.println("Il catalogo è attualmente vuoto.");
            out.println("Premi INVIO per tornare alla Home...");
            scanner.nextLine();
            return new HomeScreenState(context);
        }

        // Mostra i prodotti disponibili
        for (int i = 0; i < products.size(); i++) {
            out.print("[" + i + "] ");
            for (String detail : products.get(i).getReducedDetails()) {
                out.print(detail + " | ");
            }
            out.println();
        }

        out.print("\nSeleziona l'indice dell'articolo per i dettagli (-1 o 'B' per tornare alla Home): ");
        String input = scanner.nextLine().trim();

        // Ritorno alla Home sia con -1 che con il comando standard 'B'
        if (input.equalsIgnoreCase("B") || input.equals("-1")) {
            return new HomeScreenState(context);
        }

        try {
            int index = Integer.parseInt(input);

            if (index < 0 || index >= products.size()) {
                out.println(">> Indice non valido.");
                return this; // Ricarica il catalogo
            }

            // Salva il prodotto selezionato nel contesto e passa ai dettagli
            context.setSelectedProduct(products.get(index));
            return new ProductDetailsScreen(context);

        } catch (NumberFormatException e) {
            out.println(">> Inserimento non valido. Digita un numero corrispondente all'indice.");
            return this;
        }
    }
}