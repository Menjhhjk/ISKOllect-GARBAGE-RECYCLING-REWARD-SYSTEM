package com.iskollect.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    //properties keys
    private static final String CONFIG_FILE  = "config.properties";
    private static final String KEY_URL      = "db.url";
    private static final String KEY_USER     = "db.user";
    private static final String KEY_PASS     = "db.password";

    //PostgreSQL driver class
    private static final String PG_DRIVER    = "org.postgresql.Driver";

    //constructor
    private DBConnection() {
        try {
            Properties props = loadProperties();
            String url  = props.getProperty(KEY_URL);
            String user = props.getProperty(KEY_USER);
            String pass = props.getProperty(KEY_PASS);

            Class.forName(PG_DRIVER);
            this.connection = DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "PostgreSQL JDBC driver not found. Add org.postgresql:postgresql to pom.xml.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Failed to connect to the database. Check config.properties.", e);
        } catch (IOException e) {
            throw new RuntimeException(
                "config.properties not found on classpath.", e);
        }
    }

    //accessors
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(3)) {
                instance = new DBConnection();
                return instance.connection;
            }
        } catch (SQLException e) {
            instance = new DBConnection();
            return instance.connection;
        }
        return connection;
    }

    //for closing the db connection
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Warning: could not close DB connection — " + e.getMessage());
            } finally {
                connection = null;
                instance   = null;
            }
        }
    }

    //returns the properties of the config file
    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) throw new IOException(CONFIG_FILE + " not found on classpath.");
            props.load(in);
        }
        return props;
    }
}