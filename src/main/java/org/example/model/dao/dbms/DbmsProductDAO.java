package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.dao.dbms.utility.ConnectionFactory;
import org.example.model.entity.product.ConditionEnum;
import org.example.model.entity.product.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbmsProductDAO implements ProductDAO {

    private static final Logger LOGGER = Logger.getLogger(DbmsProductDAO.class.getName());

    // 1. Specifichiamo le colonne per evitare SELECT * (Migliora performance e sicurezza)
    private static final String PRODUCT_COLUMNS = "id, ownerEmail, title, description, dailyPrice, product_condition, category, imageUrl, available";

    private static final String INSERT_PRODUCT =
            "INSERT INTO products (" + PRODUCT_COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE title=?, description=?, dailyPrice=?, product_condition=?, category=?, imageUrl=?, available=?";

    private static final String SELECT_ALL = "SELECT " + PRODUCT_COLUMNS + " FROM products";
    private static final String SELECT_BY_ID = "SELECT " + PRODUCT_COLUMNS + " FROM products WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM products WHERE id = ?";

    @Override
    public boolean save(Product product) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PRODUCT)) {

            // Mapping per l'inserimento (1-9)
            mapProductToStatement(stmt, product, 1);

            // Mapping per l'update (10-16) - Saltiamo ID e OwnerEmail che sono chiavi
            stmt.setString(10, product.getTitle());
            stmt.setString(11, product.getDescription());
            stmt.setDouble(12, product.getDailyPrice());
            stmt.setString(13, product.getCondition().name());
            stmt.setString(14, product.getCategory());
            stmt.setString(15, product.getImageUrl());
            stmt.setBoolean(16, product.isAvailable());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel salvataggio prodotto: " , e);
            return false;
        }
    }

    @Override
    public Product getById(String id) {
        // Usiamo query mirata con PreparedStatement
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore recupero prodotto: ", e);
        }
        return null;
    }
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore recupero lista prodotti", e);
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
            LOGGER.log(Level.SEVERE, "Errore eliminazione:{id} ", e);
            return false;
        }
    }

    // --- Helper Methods per evitare ripetizioni (DRY Principle) ---

    private void mapProductToStatement(PreparedStatement stmt, Product p, int startIdx) throws SQLException {
        stmt.setString(startIdx, p.getId());
        stmt.setString(startIdx + 1, p.getOwnerEmail());
        stmt.setString(startIdx + 2, p.getTitle());
        stmt.setString(startIdx + 3, p.getDescription());
        stmt.setDouble(startIdx + 4, p.getDailyPrice());
        stmt.setString(startIdx + 5, p.getCondition().name());
        stmt.setString(startIdx + 6, p.getCategory());
        stmt.setString(startIdx + 7, p.getImageUrl());
        stmt.setBoolean(startIdx + 8, p.isAvailable());
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setOwnerEmail(rs.getString("ownerEmail"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setDailyPrice(rs.getDouble("dailyPrice"));

        try {
            p.setCondition(ConditionEnum.valueOf(rs.getString("product_condition")));
        } catch (Exception e) {
            p.setCondition(ConditionEnum.UNDEFINED);
        }

        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("imageUrl"));
        p.setAvailable(rs.getBoolean("available"));
        return p;
    }
}