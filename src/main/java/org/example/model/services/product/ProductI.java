package org.example.model.services.product;


import org.example.model.services.EntityI;
import org.example.model.services.WorkI;
import org.example.model.services.session.SessionI;
import org.example.model.services.user.UserI;

/**
 * interfaccia che definisce la struttura dati di un Prodotto.
 */
public interface ProductI extends EntityI<String>, WorkI {

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getOwnerEmail();
    void setOwnerEmail(String owner);

    double getPrice();
    void setPrice(double price);

    // Indica se il prodotto è disponibile o già noleggiato
    boolean isAvailable();
    void setAvailable(boolean available);
    default boolean canWork(SessionI sessionI, UserI userI) {
        // Se l'utente a runtime (come OwnerRole) implementa ActionsProductI, esegui!
        if (userI instanceof ActionsProductI worker) {
            return worker.execute(sessionI, this);
        }
        return false; // L'utente non sa gestire i prodotti (es. un Cliente semplice)
    }

}