package org.example.model.entity.actors.factory;

import org.example.util.enumu.Identifiable;

public enum ActorEnumFactory implements Identifiable {
    SIMPLE(1),
    STRATEGY(2);

    private final int id;

    ActorEnumFactory(int id) {this.id = id;}
    @Override
    public int getValue() {return id;}
    public static ActorEnumFactory fromValue(int id) {
        return Identifiable.fromValue(ActorEnumFactory.class, id);
    }
}