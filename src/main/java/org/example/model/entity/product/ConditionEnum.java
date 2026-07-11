package org.example.model.entity.product;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public enum ConditionEnum implements Serializable {
    UNDEFINED, // Valore di default per dati mancanti
    NEW,
    EXCELLENT,
    GOOD,
    WORN,
    DAMAGED;
    public static ConditionEnum fromValue(String value) {
        if (value == null || value.isBlank()) return UNDEFINED;
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return UNDEFINED;
        }
    }

    /**
     * Calcola il prezzo. Se la condizione è UNDEFINED,
     * non applica modifiche (moltiplicatore 1.0).
     */
    public double applyMultiplier(double basePrice) {
        double multiplier = switch (this) {
            case NEW       -> 1.2;
            case EXCELLENT -> 1.1;
            case WORN      -> 0.8;
            case DAMAGED   -> 0.5;
            case GOOD, UNDEFINED -> 1.0;
        };
        return basePrice * multiplier;
    }

    public static List<String> getNames() {
        return Arrays.stream(values()).map(Enum::name).toList();
    }
}