package org.example.model.entity.actors.factory;


public abstract class ActorFactory {
    private ActorFactory(){}

    public static ActorAbstractFactory getFactory(ActorEnumFactory type) {
        return switch (type) {
            case SIMPLE -> new SimpleActorFactory();
            case STRATEGY -> new StrategyActorFactory();
        };
    }

    public static ActorAbstractFactory getFactory(int id) {
        return getFactory(ActorEnumFactory.fromValue(id));
    }
}