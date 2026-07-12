package org.example.controller.application;

import org.example.controller.bean.CatalogBean;
import org.example.controller.bean.ProductBean;
import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;

public class TestCatalogo {

    public static void main(String[] args) {
        System.out.println("=== TEST RUN: RentalController2.getCatalog() ===\n");
        DAOManager.initializeSingleton(EnumDaoType.FILE,true);
        try {
            // Invocazione diretta del tuo metodo statico
            CatalogBean catalog = RentalController.getCatalog();

            System.out.println("✅ Esecuzione completata senza crash!");
            System.out.println("Numero di prodotti caricati nel Bean: " + catalog.getProducts().size());
            System.out.println("\n--- CONTENUTO DEL CATALOGO ---");

            if (catalog.getProducts().isEmpty()) {
                System.out.println("[Vuoto] Nessun prodotto disponibile o DAO vuoto.");
            } else {
                for (ProductBean bean : catalog.getProducts()) {
                    // Usiamo il tuo metodo getReducedDetails() per vedere cosa c'è dentro
                    System.out.println("\nCard Prodotto:");
                    for (String riga : bean.getReducedDetails()) {
                        System.out.println("  " + riga);
                    }
                }
            }

        } catch (NullPointerException e) {
            System.err.println("❌ CRASH! Il metodo è andato in NullPointerException.");
            System.err.println("Causa probabile: DAOManager.getProductDAO() o il metodo .getAll() restituiscono null.");
            System.err.println("\nEcco lo stack trace del crash:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ CRASH! Errore generico durante il caricamento:");
            e.printStackTrace();
        }

        System.out.println("\n=== FINE TEST ===");
    }
}