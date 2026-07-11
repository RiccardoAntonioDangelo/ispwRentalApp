package org.example.model.dao.filesimple;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.entity.actors.User;
import org.example.model.entity.session.Session;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseTest {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Path dbFolder = Paths.get("database");

        // Creiamo le due tabelle necessarie
        EntityTable<Session> sessionTable = new EntityTable<>("session", dbFolder, mapper);
        EntityTable<User> userTable = new EntityTable<>("user", dbFolder, mapper);

        // 1. Configurazione tabella Sessione (semplice)
        sessionTable.mapColumn("id", Session::getId);


        // 2. CONFIGURAZIONE TABELLA UTENTE (Con gestione esplicita degli attributi)

        // --- CONFIGURAZIONE IN SCRITTURA (Getter) ---
        userTable.mapColumn("email", User::getEmail)
                .mapColumn("name", User::getName);


        // --- CONFIGURAZIONE IN LETTURA (Setter per ogni attributo) ---
        userTable.unmapColumn("email", (user, node) -> user.setEmail(node.asText()))
                .unmapColumn("name", (user, node) -> user.setName(node.asText()));

        // QUI RISOLVIAMO LA FOREIGN KEY PER L'ATTRIBUTO SESSIONE


    }
}