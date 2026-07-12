package org.example.model.services.product;

import org.example.model.services.CollectionI;
import org.example.model.services.session.SessionI;

/**
 * interfaccia per il comportamento di creazione di un nuovo prodotto/articolo.
 */
public interface ActionsProductI  {
    String PRODUCT_COLLECTION_KEY = ActionsProductI.class.getSimpleName();
    default void initProductI(SessionI sessionI){
        sessionI.ensureCollection(PRODUCT_COLLECTION_KEY);
        sessionI.ensureCollection(PRODUCT_COLLECTION_KEY+1);
    }
    default void addProduct(ProductI product, SessionI sessionI){
        initProductI(sessionI);
        sessionI.addItem(PRODUCT_COLLECTION_KEY,product);
    }
    default CollectionI<ProductI> getProducts(SessionI sessionI){
        return  sessionI.getCollection(PRODUCT_COLLECTION_KEY);
    }
    default boolean execute(SessionI session, ProductI product) {
        if (session == null) {throw new IllegalArgumentException("Sessione non valida durante l'esecuzione del noleggio.");}
        addProduct(product,session);
        return true;
    }


    /**
     * Metodo per convalidare i dati del prodotto prima della creazione.
     */
    default boolean validate(String name, double price) {
        return name != null && !name.isEmpty() && price >= 0;
    }
}