package org.example.model.entity.actors.factory;

import org.example.model.entity.actors.User;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.actors.simple.Owner;

public class SimpleActorFactory implements ActorAbstractFactory {
    @Override
    public User createClient(String email, String password) {
        return new Client(email, password);
    }

    @Override
    public User createOwner(String email, String password) {
        return new Owner(email, password);
    }
}
