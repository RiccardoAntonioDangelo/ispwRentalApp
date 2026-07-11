package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.RentalDAO;
import org.example.model.dao.dbms.utility.ConnectionFactory;
import org.example.model.entity.rental.RentalOld;
import org.example.model.entity.rental.StatusEnum;
import org.example.model.services.rent.RentI;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DbmsRentalDAO implements RentalDAO {

    private static final Logger LOGGER = Logger.getLogger(DbmsRentalDAO.class.getName());

    // 1. Definiamo le colonne in una costante per coerenza tra SELECT e INSERT
    private static final String RENTAL_COLUMNS = "id, clientEmail, ownerEmail, productId, startDate, endDate, rentalStatus, totalCost, locate, paymentId";

    private static final String INSERT_RENTAL =
            "INSERT INTO rentals (" + RENTAL_COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE rentalStatus=?, totalCost=?, locate=?, paymentId=?";

    private static final String SELECT_BY_ID = "SELECT " + RENTAL_COLUMNS + " FROM rentals WHERE id = ?";
    private static final String SELECT_BY_USER = "SELECT " + RENTAL_COLUMNS + " FROM rentals WHERE clientEmail = ?";
    private static final String DELETE_BY_ID = "DELETE FROM rentals WHERE id = ?";

    @Override
    public boolean save(RentI rental) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_RENTAL)) {

            // Mapping Inserimento (Parametri 1-10)
            mapRentalToStatement(stmt, rental);

            // Mapping Update (Parametri 11-14)
            stmt.setString(11, rental.getStatus().name());
            //stmt.setDouble(12, rental.getTotalCost());TODO
            //stmt.setString(13, rental.getLocate());
            //stmt.setString(14, rental.getPayment());TODO

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Errore salvataggio noleggio: " + rental.getId(), e);
            return false;
        }
    }

    @Override
    public RentalOld getById(String id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRowToRental(rs);
            }
        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Errore recupero noleggio: " + id, e);
        }
        return null;
    }


    public List<RentI> getAll() {
        // Se l'interfaccia richiede getAll(), puoi implementarlo qui o lasciarlo vuoto
        return new ArrayList<>();
    }

    public List<RentalOld> getRentalsByUserId(String userId) {
        List<RentalOld> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToRental(rs));
                }
            }
        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Errore recupero noleggi utente: " + userId, e);
        }
        return list;
    }

    @Override
    public boolean delete(String id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BY_ID)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            //LOGGER.log(Level.SEVERE, "Errore eliminazione noleggio: " + id, e);
            return false;
        }
    }

    // --- Helper Methods ---

    private void mapRentalToStatement(PreparedStatement stmt, RentI r) throws SQLException {
        stmt.setString(1, r.getId());
        stmt.setString(2, r.getClientEmail());
        stmt.setString(3, r.getOwnerEmail());
        stmt.setString(4, r.getProductId());
        stmt.setDate(5, Date.valueOf(r.getStartDate()));
        stmt.setDate(6, Date.valueOf(r.getEndDate()));
        stmt.setString(7, r.getStatus().name());
        //stmt.setDouble(8, r.getTotalCost());TODO
        //stmt.setString(9, r.getLocate());
        //stmt.setString(10, r.getPayment());TODO
    }

    private RentalOld mapRowToRental(ResultSet rs) throws SQLException {
        RentalOld rental = new RentalOld();
        rental.setClientEmail(rs.getString("clientEmail"));
        rental.setOwnerEmail(rs.getString("ownerEmail"));
        rental.setProductId(rs.getString("productId"));
        rental.setLocate(rs.getString("locate"));
        //rental.setPayment(rs.getString("paymentId"));
        //rental.setTotalCost(rs.getDouble("totalCost"));

        // Conversione sicura Date -> LocalDate
        Date start = rs.getDate("startDate");
        if (start != null) rental.setStartDate(start.toLocalDate());

        Date end = rs.getDate("endDate");
        if (end != null) rental.setEndDate(end.toLocalDate());

        // Mapping Enum con fallback
        try {
            rental.setStatus(StatusEnum.valueOf(rs.getString("rentalStatus")));
        } catch (Exception e) {
            rental.setStatus(StatusEnum.PENDING);
        }

        return rental;
    }
}