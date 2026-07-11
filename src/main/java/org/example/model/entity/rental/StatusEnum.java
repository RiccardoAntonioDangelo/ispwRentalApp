package org.example.model.entity.rental;

import java.util.Arrays;
import java.util.List;

public enum StatusEnum {
    PENDING,
    APPROVED,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    REJECTED;

    /**
     * Parsing sicuro.
     * Converte una stringa (es. da filedeprecated o input) nell'Enum corrispondente.
     */
    public static StatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return PENDING;
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }

    /**
     * Logica di Business: definisce se un noleggio è in uno stato
     * che permette l'annullamento.
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == APPROVED;
    }

    /**
     * Restituisce tutti i nomi degli stati (utile per filtri o ComboBox).
     */
    public static List<String> getNames() {
        return Arrays.stream(values()).map(Enum::name).toList();
    }
}