package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private static final String PATH_PROPERTIES_FILE = "db.properties";
    private static Object lock = new Object();
    private String url;
    private String username;
    private String password;

    public DatabaseConnection() {
        loadDataSourceConfiguration();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }


    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadDataSourceConfiguration() {
        try (InputStream resourceAsStream =
                     DatabaseConnection.class.getClassLoader().getResourceAsStream(PATH_PROPERTIES_FILE)) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            url = properties.getProperty("javabase.jdbc.url");
            username = properties.getProperty("javabase.jdbc.username");
            password = properties.getProperty("javabase.jdbc.password");
            final String driver = properties.getProperty("javabase.jdbc.driver");

            if (url == null || username == null || password == null || driver == null) {
                throw new RuntimeException("Данные из конфигурационного файла не прочитаны или прочитаны не полностью");
            }
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
