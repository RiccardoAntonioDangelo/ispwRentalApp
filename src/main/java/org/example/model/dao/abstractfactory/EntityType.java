package org.example.model.dao.abstractfactory;

import org.example.model.entity.actors.User;
import org.example.model.entity.product.Product;
import org.example.model.entity.rental.state.RentalS;
import org.example.model.entity.session.Session;
import org.example.util.enumu.Identifiable;

import java.util.HashMap;
import java.util.Map;

public enum EntityType implements Identifiable {
    USER(1, User.class),
    PRODUCT(2, Product.class),
    RENTAL(3, RentalS.class),
    SESSION(4, Session.class);

    private final int id;
    private final Class<?> entityClass;

    // Mappa per il lookup inverso: Classe -> Enum
    private static final Map<Class<?>, EntityType> CLASS_MAP = new HashMap<>();

    static {
        for (EntityType type : values()) {
            CLASS_MAP.put(type.entityClass, type);
        }
    }

    EntityType(int id, Class<?> entityClass) {
        this.id = id;
        this.entityClass = entityClass;
    }

    @Override
    public int getValue() { return id; }

    /**
     * Ritorna l'EntityType data la classe dell'entità.
     */
    public static EntityType fromClass(Class<?> clazz) {
        EntityType type = CLASS_MAP.get(clazz);
        if (type == null) {
            throw new IllegalArgumentException("Nessun EntityType associato alla classe: " + clazz.getSimpleName());
        }
        return type;
    }

    public static EntityType fromValue(int id) {
        return Identifiable.fromValue(EntityType.class, id);
    }
}