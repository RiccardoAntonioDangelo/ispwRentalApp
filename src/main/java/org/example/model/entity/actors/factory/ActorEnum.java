package org.example.model.entity.actors.factory;

import org.example.util.str.StrApp;
import java.util.Arrays;
import java.util.List;

public enum ActorEnum {
    // Aggiunto il flag booleano alla fine di ogni istanza
    CLIENT(StrApp.ROLE_CLIENT_VAL, StrApp.ROLE_CLIENT_NAME, StrApp.ROLE_CLIENT_DESC, true),
    OWNER(StrApp.ROLE_OWNER_VAL, StrApp.ROLE_OWNER_NAME, StrApp.ROLE_OWNER_DESC, true),
    USER("", "", "", false); // USER ha 'false', non verrà usato nella UI

    private final String value;
    private final String displayName;
    private final String description;
    private final boolean graphicUsable; // Nuovo campo booleano

    // Costruttore aggiornato
    ActorEnum(String value, String displayName, String description, boolean graphicUsable) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
        this.graphicUsable = graphicUsable;
    }

    public String[] toGraphicVector() {
        return new String[]{this.value, this.displayName, this.description};
    }

    // Getter per controllare se è usabile graficamente
    public boolean isGraphicUsable() {
        return graphicUsable;
    }

    public String getValue() { return value; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    public static List<String> names() {
        return Arrays.stream(values()).map(Enum::name).toList();
    }

    public static ActorEnum fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Il valore del ruolo non può essere nullo");
        }

        String cleanValue = value.trim();

        return Arrays.stream(values())
                .filter(actor -> actor.getValue().equalsIgnoreCase(cleanValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nessun ActorEnum trovato per il valore: " + cleanValue));
    }
}