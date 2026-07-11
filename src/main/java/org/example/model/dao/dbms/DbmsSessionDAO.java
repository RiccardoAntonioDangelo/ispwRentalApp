package org.example.model.dao.dbms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.dao.dbms.utility.ConnectionFactory;
import org.example.model.entity.LazyEntityList;
import org.example.model.entity.actors.User;
import org.example.model.entity.session.Session;
import org.example.model.services.CollectionI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbmsSessionDAO implements SessionDAO {

    private static final Logger LOGGER = Logger.getLogger(DbmsSessionDAO.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Definiamo le colonne esplicite
    private static final String SESSION_COLUMNS = "user_email, lazy_collections";

    private static final String INSERT_SESSION =
            "INSERT INTO sessions (" + SESSION_COLUMNS + ") VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE lazy_collections = ?";

    private static final String SELECT_BY_ID = "SELECT " + SESSION_COLUMNS + " FROM sessions WHERE user_email = ?";
    private static final String DELETE_BY_ID = "DELETE FROM sessions WHERE user_email = ?";
    private static final String SELECT_ALL = "SELECT " + SESSION_COLUMNS + " FROM sessions";

    @Override
    public boolean save(Session session) {
        if (session == null || session.getId() == null) return false;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SESSION)) {

            // Trasformiamo la mappa delle liste lazy in una stringa JSON
            String jsonCollections = MAPPER.writeValueAsString(session.getLazyCollections());

            stmt.setString(1, session.getId());
            stmt.setString(2, jsonCollections);
            stmt.setString(3, jsonCollections); // Per l'update in caso di duplicato

            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Session getById(String id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRowToSession(rs);
            }
        } catch (Exception e) {
            //LOGGER.log(Level.SEVERE, "Errore recupero sessione: " + id, e);
        }
        return null;
    }
    public List<Session> getAll() {
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sessions.add(mapRowToSession(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore recupero tutte le sessioni", e);
        }
        return sessions;
    }

    @Override
    public boolean delete(String id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BY_ID)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            //LOGGER.log(Level.SEVERE, "Errore eliminazione sessione: " + id, e);
            return false;
        }
    }

    // --- Helper Method per la ricostruzione dell'oggetto ---

    private Session mapRowToSession(ResultSet rs) throws SQLException, JsonProcessingException {
        String email = rs.getString("user_email");
        String json = rs.getString("lazy_collections");

        // Qui creiamo un utente "placeholder" (Proxy-like) che contiene l'email
        // In un sistema reale, potresti voler caricare l'utente completo dal UserDAO
        User user = new User();
        user.setEmail(email);

        Session session = new Session(user);

        if (json != null && !json.isEmpty()) {
            // Jackson ricostruisce la mappa complessa usando la Reflection
            Map<String, CollectionI<?>> collections = MAPPER.readValue(json,
                    new TypeReference<>() {});
            session.setLazyCollections(collections);
        }

        return session;
    }
}