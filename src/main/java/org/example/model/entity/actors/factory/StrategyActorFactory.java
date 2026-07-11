package org.example.model.entity.actors.factory;

import org.example.model.entity.actors.User;
import org.example.model.entity.actors.strategy.ClientRole;
import org.example.model.entity.actors.strategy.OwnerRole;
import org.example.model.entity.actors.strategy.UserStrategy;


public class StrategyActorFactory implements ActorAbstractFactory {
    @Override
    public User createClient(String email, String password) {return new UserStrategy(email, password, new ClientRole());}
    @Override
    public User createOwner(String email, String password) {return new UserStrategy(email, password, new OwnerRole());}
}