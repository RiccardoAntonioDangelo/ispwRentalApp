package org.example.util.enumu;

public class EnumResolver {
    private EnumResolver() {}

    public static <T extends Enum<T> & Identifiable> T fromValue(Class<T> enumClass, int id) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.getValue() == id) {
                return constant;
            }
        }
        throw new IllegalArgumentException(
                String.format("Valore %d non supportato per l'enum %s", id, enumClass.getSimpleName())
        );
    }
}