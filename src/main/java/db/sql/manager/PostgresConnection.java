package db.sql.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {
    private static PostgresConnection instance;

    private final String url;
    private final String username;
    private final String password;

    private Connection connection;

    private PostgresConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static void createInstance(String url, String username, String password) throws SQLException {
        instance = new PostgresConnection(url, username, password);
        instance.connect();
        PostgresManager.createTables();
    }

    public static PostgresConnection getInstance() {
        return instance;
    }

    private void connect() throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public Connection getConnection() {
        return connection;
    }
}
