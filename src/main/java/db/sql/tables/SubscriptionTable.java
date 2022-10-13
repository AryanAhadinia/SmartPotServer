package db.sql.tables;

import db.sql.interfaces.ManyToManyTable;
import db.sql.interfaces.PostgresConnectable;
import model.Device;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionTable implements ManyToManyTable<User, Device> {
    private static SubscriptionTable instance;

    public static final String SUBSCRIPTION_TABLE = "subscription";

    public static final String USER_TABLE = UserTable.USER_TABLE;
    public static final String USER_EMAIL = UserTable.USER_EMAIL;
    public static final String DEVICE_TABLE = DeviceTable.DEVICE_TABLE;
    public static final String DEVICE_SERIAL = DeviceTable.DEVICE_SERIAL;

    private SubscriptionTable() {
    }

    public static SubscriptionTable getInstance() {
        if (instance == null)
            return instance = new SubscriptionTable();
        return instance;
    }

    @Override
    public void createTable() throws SQLException {
        String sql = "create table if not exists " + SUBSCRIPTION_TABLE + " (" +
                USER_EMAIL + " varchar references " + USER_TABLE + " on delete cascade on update cascade, " +
                DEVICE_SERIAL + " int references " + DEVICE_TABLE + " on delete cascade on update cascade, " +
                "primary key (" + USER_EMAIL + ", " + DEVICE_SERIAL + "));";
        try (Statement statement = PostgresConnectable.getConnection().createStatement()) {
            statement.execute(sql);
        }
    }

    @Override
    public void insert(User user, Device device) throws SQLException {
        String sql = "insert into " + SUBSCRIPTION_TABLE + " values (?, ?);";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setInt(2, device.getDeviceSerial());
            statement.execute();
        }
    }

    @Override
    public boolean isRelated(User user, Device device) throws SQLException {
        String sql = "select * from " + SUBSCRIPTION_TABLE + " where " + USER_EMAIL + "=? and " + DEVICE_SERIAL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setInt(2, device.getDeviceSerial());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public List<Device> getRelated1(User user) throws SQLException {
        String sql = "select * from " + SUBSCRIPTION_TABLE + " where " + USER_EMAIL + "=?;";
        ArrayList<Device> devices = new ArrayList<>();
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int deviceSerial = resultSet.getInt(resultSet.findColumn(DEVICE_SERIAL));
                    devices.add(DeviceTable.getInstance().read(Device.getInstance(deviceSerial, null, null)));
                }
            }
        }
        return devices;
    }

    @Override
    public List<User> getRelated2(Device device) throws SQLException {
        String sql = "select * from " + SUBSCRIPTION_TABLE + " where " + DEVICE_SERIAL + "=?;";
        ArrayList<User> users = new ArrayList<>();
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setInt(1, device.getDeviceSerial());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                String userEmail = resultSet.getString(resultSet.findColumn(USER_EMAIL));
                users.add(UserTable.getInstance().read(User.getInstance(userEmail, null)));
                }
            }
        }
        return users;
    }

    @Override
    public void delete(User user, Device device) throws SQLException {
        String sql = "delete from " + SUBSCRIPTION_TABLE + " where " + USER_EMAIL + "=? and " + DEVICE_SERIAL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setInt(2, device.getDeviceSerial());
            statement.execute();
        }
    }

    @Override
    public void delete1(User user) throws SQLException {
        String sql = "delete from " + SUBSCRIPTION_TABLE + " where " + USER_EMAIL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.execute();
        }
    }

    @Override
    public void delete2(Device device) throws SQLException {
        String sql = "delete from " + SUBSCRIPTION_TABLE + " where " + DEVICE_SERIAL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setInt(1, device.getDeviceSerial());
            statement.execute();
        }
    }
}
