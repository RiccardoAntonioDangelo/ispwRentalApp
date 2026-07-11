package org.example.model.entity.actors.factory.test;

import org.example.model.entity.actors.User;
import org.example.model.entity.actors.factory.*;
import org.example.model.entity.actors.simple.Owner;
import org.example.model.entity.actors.strategy.UserStrategy;
import org.example.model.entity.product.Product;
import org.example.model.entity.rental.RentalOld;
import org.example.model.entity.session.Session;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- TEST ARCHITETTURA FACTORY ---");

        // 1. TEST VERSIONE STRATEGY (Composizione)
        System.out.println("\n[Test Strategy]");
        ActorAbstractFactory strategyFactory = ActorFactory.getFactory(ActorEnumFactory.STRATEGY);
        UserStrategy userStrategy =(UserStrategy) strategyFactory.createOwner("owner@strategy.com", "pass123");
        Session session=new Session(userStrategy);
        System.out.println("Classe dell'oggetto: " + userStrategy.getClass().getSimpleName());
        System.out.println("Classe dell'oggetto: " + userStrategy.execute(session,new RentalOld()));
        System.out.println("Classe dell'oggetto: " + userStrategy.execute( session,  new Product( )));



        // ---

        // 2. TEST VERSIONE SIMPLE (Ereditarietà)
        System.out.println("\n[Test Simple]");
        ActorAbstractFactory simpleFactory = ActorFactory.getFactory(ActorEnumFactory.SIMPLE);
        User userSimple = simpleFactory.createOwner("owner@simple.com", "pass456");
        
        System.out.println("Classe dell'oggetto: " + userSimple.getClass().getSimpleName());
        
        // In questo caso, l'oggetto STESSO è un'istanza di Owner
        if (userSimple instanceof Owner) {
            System.out.println("Successo: L'utente Simple è un'istanza della sottoclasse Owner.");
        }

        // ---

        // 3. TEST CAMBIO RUOLO A RUNTIME (Solo possibile con Strategy)
        System.out.println("\n[Test Flessibilità Strategy]");
        // Immaginiamo che il primo utente voglia diventare un semplice Client
        userStrategy.setRole(new org.example.model.entity.actors.strategy.ClientRole());
        System.out.println("Nuovo ruolo dell'utente Strategy: " + userStrategy);
    }
}