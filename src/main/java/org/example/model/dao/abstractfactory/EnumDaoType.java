package org.example.model.dao.abstractfactory;

import org.example.util.enumu.Identifiable;

public enum EnumDaoType implements Identifiable {
    DEMO(1),
    FILE(2),
    DBMS(3);

    private final int id;

    EnumDaoType(int id) {
        this.id = id;
    }

    @Override
    public int getValue() {return id;}
    public static EnumDaoType fromValue(int id) {
        return Identifiable.fromValue(EnumDaoType.class, id);
    }
}