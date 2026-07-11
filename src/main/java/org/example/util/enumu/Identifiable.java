package org.example.util.enumu;

public interface Identifiable {
    int getValue();

    default boolean is(int id) {
        return getValue() == id;
    }

    default String getName() {
        return ((Enum<?>) this).name();
    }
    static  <T extends Enum<T> & Identifiable>  T fromValue( Class<T> enumClass,int id) {
        return EnumResolver.fromValue(enumClass, id);
    }

}