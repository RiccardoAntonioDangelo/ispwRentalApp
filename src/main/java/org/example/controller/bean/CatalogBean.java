package org.example.controller.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogBean {
    private List<ProductBean> products = new ArrayList<>();

    /**
     * Restituisce una vista non modificabile della lista dei prodotti.
     * La View può leggerli, ma non può alterare il catalogo direttamente.
     */
    public List<ProductBean> getProducts() {
        return Collections.unmodifiableList(products);
    }

    /**
     * Imposta i prodotti garantendo la non-nullità della lista.
     */
    public void setProducts(List<ProductBean> products) {
        this.products = (products != null) ? new ArrayList<>(products) : new ArrayList<>();
    }

    /**
     * Aggiunge un singolo prodotto.
     */
    public void addProduct(ProductBean product) {
        if (product != null) {
            this.products.add(product);
        }
    }

    /**
     * Rimuove un singolo prodotto.
     */
    public void removeProduct(ProductBean product) {
        this.products.remove(product);
    }

    /**
     * Svuota completamente il catalogo.
     */
    public void clear() {
        this.products.clear();
    }
}