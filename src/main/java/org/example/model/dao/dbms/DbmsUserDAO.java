package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.dao.dbms.utility.ConnectionFactory;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.actors.simple.Owner;
import org.example.model.entity.actors.User;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbmsUserDAO implements UserDAO {
    private static final String SELECT_ALL_USERS =
            "SELECT u.email, u.name, u.surname, u.password, u.phone, " +
                    "CASE " +
                    "    WHEN o.email IS NOT NULL THEN 'OWNER' " +
                    "    WHEN c.email IS NOT NULL THEN 'CLIENT' " +
                    "    ELSE 'USER' " +
                    "END AS user_type " +
                    "FROM users u " +
                    "LEFT JOIN owners o ON u.email = o.email " +
                    "LEFT JOIN clients c ON u.email = c.email";

    private static final Logger LOGGER = Logger.getLogger(DbmsUserDAO.class.getName());
    private static final String USER_COLUMNS = "email, name, surname, password, phone";

    private static final String INSERT_USER =
            "INSERT INTO users (" + USER_COLUMNS + ") VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE name=?, surname=?, password=?, phone=?";

    private static final String SELECT_USER_BY_EMAIL = "SELECT " + USER_COLUMNS + " FROM users WHERE email = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE email = ?";

    @Override
    public boolean save(User user) {
        if (user == null || user.getEmail() == null) return false;

        // Gestiamo tutto in un unico blocco try-with-resources
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            // Eseguiamo le operazioni in sequenza
            executeSave(conn, user);
            saveActorSpecialization(user, conn);

            conn.commit();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio: " + user.getEmail(), e);
            // Il rollback avviene solo se la connessione è valida e non è andata in crash
            return false;
        }
    }

    /**
     * Metodo helper per separare lo statement dalla gestione connessione
     */
    private void executeSave(Connection conn, User user) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_USER)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getSurname());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getName());
            stmt.setString(7, user.getSurname());
            stmt.setString(8, user.getPassword());
            stmt.setString(9, user.getPhone());
            stmt.executeUpdate();
        }
    }

    @Override
    public User getById(String email) {
        // Flattening del try-with-resources per multipli oggetti
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_EMAIL)) {

            stmt.setString(1, email);
            return fetchUser(stmt, conn);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore recupero utente: " + email, e);
            return null;
        }
    }

    private User fetchUser(PreparedStatement stmt, Connection conn) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapRowToUser(rs, conn);
            }
        }
        return null;
    }

    @Override
    public boolean delete(String email) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore eliminazione utente: " + email, e);
            return false;
        }
    }

    // --- I restanti metodi rimangono simili ma chiamati dai blocchi sopra ---

    private User mapRowToUser(ResultSet rs, Connection conn) throws SQLException {
        String email = rs.getString("email");
        User user = resolveUserInstance(conn, email);

        user.setEmail(email);
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));

        return user;
    }

    private User resolveUserInstance(Connection conn, String email) throws SQLException {
        if (checkExistsInTable(conn, "owners", email)) return new Owner();
        if (checkExistsInTable(conn, "clients", email)) return new Client();
        return new User();
    }

    private boolean checkExistsInTable(Connection conn, String tableName, String email) throws SQLException {
        String query = "SELECT 1 FROM " + tableName + " WHERE email = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void saveActorSpecialization(User user, Connection conn) throws SQLException {
        String table;

        if (user instanceof Owner) {
            table = "owners";
        } else if (user instanceof Client) {
            table = "clients";
        } else {
            table = null;
        }

        if (table != null) {
            String sql = "INSERT IGNORE INTO " + table + " (email) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getEmail());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public List<User> getAll() {
        List<User> users = new java.util.ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUserWithRole(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti gli utenti", e);
        }

        return users;
    }
    private User mapRowToUserWithRole(ResultSet rs) throws SQLException {
        String type = rs.getString("user_type");
        User user;

        // Istanziamo l'oggetto corretto in base alla colonna calcolata
        switch (type) {
            case "OWNER" -> user = new Owner();
            case "CLIENT" -> user = new Client();
            default -> user = new User();
        }

        // Popoliamo i dati comuni
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));

        return user;
    }
}