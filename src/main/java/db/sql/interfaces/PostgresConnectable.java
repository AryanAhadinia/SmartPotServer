package db.sql.interfaces;

import db.sql.manager.PostgresConnection;

import java.sql.Connection;

public interface PostgresConnectable {

    static Connection getConnection() {
        return PostgresConnection.getInstance().getConnection();
    }
}
