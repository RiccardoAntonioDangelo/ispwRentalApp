package org.example.controller.bean;

import org.example.model.entity.product.Product;
import org.example.model.services.product.ProductI;
import org.example.util.str.StrApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper per la View. Incapsula completamente i dati del modello
 * esponendoli solo tramite liste strutturate.
 */
public class ProductBean {
    private final ProductI product;

    public ProductBean(ProductI product) {
        this.product = product;
    }

    public ProductI getProduct() {
        return product;
    }

    public Product getConcreteProduct() {
        if (product instanceof Product) {
            return (Product) product;
        }
        return null;
    }

    // =========================================================================
    // METODI PRIVATI DI LOGICA INTERNA (Nascosti alla View)
    // =========================================================================

    public String getId() { return product.getId(); }
    public String getTitle() { return product.getName(); }
    private String getDescription() { return product.getDescription(); }
    private String getStrDailyPrice() { return product.getPrice() + StrApp.CURRENCY_EUR; }//todo
    public String getOwnerEmail() {return product.getOwnerEmail();}

    private String getTotalPrice() {return StrApp.money(product.getPrice() * 7);}

    //private  String getSubTitle() { return  "" ; }
    private String getDiscount() { return "0" + StrApp.SUFFIX_PERCENT; }

    // =========================================================================
    // UNICA INTERFACCIA PUBBLICA PER LA VIEW
    // =========================================================================

    /**
     * VERSIONE CORTA: Argomenti essenziali per le card del catalogo.
     */
    public List<String> getReducedDetails() {
        List<String> details = new ArrayList<>();
        // MODIFICA: Uso delle nuove costanti di parsing dedicate alla UI
        details.add(StrApp.PREFIX_TITLE + getTitle());
        details.add(StrApp.PREFIX_SUBTITLE + getDescription());
        details.add(StrApp.PREFIX_DAILY_PRICE + getStrDailyPrice());
        details.add(StrApp.PREFIX_DISCOUNT + getDiscount());
        details.add(StrApp.PREFIX_TOTAL_PRICE + getTotalPrice());
        return details;
    }

    /**
     * VERSIONE LUNGA: Tutti gli argomenti per la schermata di dettaglio completa.
     */
    public List<String> getCompleteDetails() {
        List<String> details = new ArrayList<>(getReducedDetails());

        // MODIFICA: Mappatura coerente con i nuovi prefissi stringa
        details.add(StrApp.PREFIX_ID + getId());
        details.add(StrApp.PREFIX_DESCRIPTION + getDescription());

        String availability = product.isAvailable() ? StrApp.STATUS_AVAILABLE : StrApp.STATUS_UNAVAILABLE;
        details.add(StrApp.LABEL_AVAILABILITY_PREFIX + availability);

        if (product instanceof Product concrete) {
            details.add(StrApp.LABEL_OWNER_EMAIL_PREFIX + concrete.getOwnerEmail());
            details.add(StrApp.LABEL_CATEGORY_PREFIX + concrete.getCategory());

            if (concrete.getSpecifications() != null && !concrete.getSpecifications().isEmpty()) {
                details.add(StrApp.PREFIX_SPECS + String.join(", ", concrete.getSpecifications()));
            }

            if (concrete.getRentalConditions() != null && !concrete.getRentalConditions().isEmpty()) {
                details.add(StrApp.PREFIX_CONDITIONS + String.join(", ", concrete.getRentalConditions()));
            }
        }

        return details;
    }
}