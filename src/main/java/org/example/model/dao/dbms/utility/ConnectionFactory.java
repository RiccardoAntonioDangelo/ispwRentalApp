package org.example.model.dao.dbms.utility;

import org.example.exceptions.dao.dbms.DatabaseConfigurationException;
import org.example.exceptions.dao.dbms.DatabaseDriverException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static final Properties properties = new Properties();
    private static final String DB_PROPERTIES_FILE = "db.properties";
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_USER_KEY = "db.user";
    private static final String DB_PASSWORD_KEY = "db.password";
    private static final String DB_DRIVER_KEY = "db.driver";
    private ConnectionFactory() {}

    static {loadProperties();}
    private static void loadProperties() {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (input == null) {throw new DatabaseConfigurationException("Impossibile trovare il file: " + DB_PROPERTIES_FILE);}

            properties.load(input);
            String driverClass = properties.getProperty(DB_DRIVER_KEY);

            if (driverClass == null || driverClass.isBlank()) {throw new DatabaseConfigurationException("Driver class non specificata in " + DB_PROPERTIES_FILE);}

            Class.forName(driverClass);

        } catch (IOException | ClassNotFoundException | DatabaseConfigurationException e) {
            throw new DatabaseDriverException("Errore critico durante l'inizializzazione del database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty(DB_URL_KEY),
                properties.getProperty(DB_USER_KEY),
                properties.getProperty(DB_PASSWORD_KEY)
        );
    }
}
