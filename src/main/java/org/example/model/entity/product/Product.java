package org.example.model.entity.product;

import org.example.model.services.product.ProductI;
import org.example.util.str.StrApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product implements ProductI {
    private String ownerEmail;
    private String title;
    private String description;
    private double dailyPrice;
    private ConditionEnum condition = ConditionEnum.UNDEFINED;
    private String category;
    private String imageUrl;
    private boolean available = true;

    private List<String> specifications = new ArrayList<>();
    private List<String> rentalConditions = new ArrayList<>();

    public Product() {}

    // Costruttore rapido
    public Product(String ownerEmail, String title, double dailyPrice) {
        this.ownerEmail = ownerEmail;
        this.title = title;
        this.dailyPrice = dailyPrice;
    }

    public void commitChange() {
        this.notifyObservers(this);
    }

    // ========================================
    // METODI PER ARGOMENTI IN STRINGA (Sincronizzati con il Bean tramite StrApp)
    // ========================================

    /**
     * VERSIONE CORTA: Argomenti essenziali per il catalogo con prefissi stabili.
     */
    public List<String> getReducedDetails() {
        List<String> details = new ArrayList<>();
        details.add(StrApp.PREFIX_TITLE + this.title);
        details.add(StrApp.PREFIX_DAILY_PRICE + this.dailyPrice);
        details.add(StrApp.LABEL_CATEGORY_PREFIX + this.category);
        details.add(StrApp.LABEL_WEEKLY_PRICE_PREFIX + (this.dailyPrice * 7));
        details.add(StrApp.PREFIX_DISCOUNT + "0%");
        return details;
    }

    /**
     * VERSIONE LUNGA: Tutti i dettagli del prodotto.
     */
    public List<String> getCompleteDetails() {
        List<String> details = new ArrayList<>(getReducedDetails());
        details.add(StrApp.PREFIX_ID + getId());
        details.add(StrApp.LABEL_OWNER_EMAIL_PREFIX + this.ownerEmail);
        details.add(StrApp.PREFIX_DESCRIPTION + this.description);
        details.add(StrApp.LABEL_AVAILABILITY_PREFIX + (this.available ? StrApp.STATUS_AVAILABLE : StrApp.STATUS_UNAVAILABLE));

        if (this.specifications != null && !this.specifications.isEmpty()) {
            details.add(StrApp.PREFIX_SPECS + String.join(", ", this.specifications));
        }
        if (this.rentalConditions != null && !this.rentalConditions.isEmpty()) {
            details.add(StrApp.PREFIX_CONDITIONS + String.join(", ", this.rentalConditions));
        }
        return details;
    }

    // ========================================
    // GETTERS & SETTERS IN METHOD CHAINING (Fluent API) con Auto-Persistenza
    // ========================================

    @Override
    public String getOwnerEmail() { return ownerEmail; }

    @Override
    public void setOwnerEmail(String ownerEmail) {
        if (!Objects.equals(this.ownerEmail, ownerEmail)) {
            this.ownerEmail = ownerEmail;
            this.commitChange();
        }
    }

    public String getTitle() { return title; }

    public Product setTitle(String title) {
        if (!Objects.equals(this.title, title)) {
            this.title = title;
            this.commitChange();
        }
        return this;
    }

    @Override
    public String getName() {
        return this.title;
    }

    @Override
    public void setName(String name) {
        this.setTitle(name);
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        if (!Objects.equals(this.description, description)) {
            this.description = description;
            this.commitChange();
        }
    }

    @Override
    public double getPrice() {
        return this.dailyPrice;
    }

    @Override
    public void setPrice(double price) {
        this.setDailyPrice(price);
    }

    public double getDailyPrice() { return dailyPrice; }

    public Product setDailyPrice(double dailyPrice) {
        if (Double.compare(this.dailyPrice, dailyPrice) != 0) {
            this.dailyPrice = dailyPrice;
            this.commitChange();
        }
        return this;
    }

    public ConditionEnum getCondition() { return condition; }

    public Product setCondition(ConditionEnum condition) {
        if (this.condition != condition) {
            this.condition = condition;
            this.commitChange();
        }
        return this;
    }

    public String getCategory() { return category; }

    public Product setCategory(String category) {
        if (!Objects.equals(this.category, category)) {
            this.category = category;
            this.commitChange();
        }
        return this;
    }

    public String getImageUrl() { return imageUrl; }

    public Product setImageUrl(String imageUrl) {
        if (!Objects.equals(this.imageUrl, imageUrl)) {
            this.imageUrl = imageUrl;
            this.commitChange();
        }
        return this;
    }

    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) {
        if (this.available != available) {
            this.available = available;
            this.commitChange();
        }
    }



    public List<String> getSpecifications() { return specifications; }

    public Product setSpecifications(List<String> specifications) {
        if (!Objects.equals(this.specifications, specifications)) {
            this.specifications = specifications;
            this.commitChange();
        }
        return this;
    }

    public List<String> getRentalConditions() { return rentalConditions; }

    public Product setRentalConditions(List<String> rentalConditions) {
        if (!Objects.equals(this.rentalConditions, rentalConditions)) {
            this.rentalConditions = rentalConditions;
            this.commitChange();
        }
        return this;
    }

    // ========================================
    // IDENTIFICATIVO UNICO (EntityI)
    // ========================================
    @Override
    public String getId() {
        return ownerEmail + "I" + title;
    }
}