package db.sql.tables;

import db.sql.interfaces.PostgresConnectable;
import db.sql.interfaces.Table;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserTable implements Table<User> {
    private static UserTable instance;

    public static final String USER_TABLE = "user_account";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD_HASH = "password_hash";

    private UserTable() {
    }

    public static UserTable getInstance() {
        if (instance == null)
            instance = new UserTable();
        return instance;
    }

    @Override
    public void createTable() throws SQLException {
        String sql = "create table if not exists " + USER_TABLE + " (" +
                USER_EMAIL + " varchar primary key, " +
                USER_PASSWORD_HASH + " varchar not null);";
        try (Statement statement = PostgresConnectable.getConnection().createStatement()) {
            statement.execute(sql);
        }
    }

    @Override
    public void insert(User instance) throws SQLException {
        String sql = "insert into " + USER_TABLE + " values (?, ?);";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, instance.getEmail());
            statement.setString(2, instance.getPasswordHash());
            statement.execute();
        }
    }

    @Override
    public User read(User partialInstance) throws SQLException {
        String sql = "select * from " + USER_TABLE + " where " + USER_EMAIL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, partialInstance.getEmail());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next())
                    return User.getInstance(resultSet.getString(resultSet.findColumn(USER_EMAIL)),
                            resultSet.getString(resultSet.findColumn(USER_PASSWORD_HASH)));
                return null;
            }
        }
    }

    @Override
    public void update(User instance) throws SQLException {
        String sql = "update " + USER_TABLE + " set " + USER_PASSWORD_HASH + "=? where " + USER_EMAIL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, instance.getPasswordHash());
            statement.setString(2, instance.getEmail());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(User instance) throws SQLException {
        String sql = "delete from " + USER_TABLE + " where " + USER_EMAIL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, instance.getEmail());
            statement.execute();
        }
    }
}
