package model;

import db.sql.tables.UserTable;

import java.sql.SQLException;

public class User {
    private final String email;
    private String passwordHash;

    private User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public static User getInstance(String email, String passwordHash) {
        return new User(email, passwordHash);
    }

    public void save() throws SQLException {
        UserTable.getInstance().insert(this);
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) throws SQLException {
        this.passwordHash = passwordHash;
        UserTable.getInstance().update(this);
    }

    public static User getUserByEmail(String email) throws SQLException {
        return UserTable.getInstance().read(new User(email, null));
    }

    public void delete() throws SQLException {
        UserTable.getInstance().delete(this);
    }

    public static String hashPassword(String password) {
        return password; // TODO
    }
}
