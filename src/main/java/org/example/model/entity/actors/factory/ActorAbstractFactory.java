package org.example.model.entity.actors.factory;
import org.example.model.entity.actors.User;

public interface ActorAbstractFactory {
    User createClient(String email, String password);
    User createOwner(String email, String password);
}