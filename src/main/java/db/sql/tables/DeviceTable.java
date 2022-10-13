package db.sql.tables;

import db.sql.interfaces.PostgresConnectable;
import db.sql.interfaces.Table;
import model.Device;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DeviceTable implements Table<Device> {
    private static DeviceTable instance;

    public static final String DEVICE_TABLE = "device";
    public static final String DEVICE_SERIAL = "deviceSerial";
    public static final String DEVICE_PUBLIC_KEY = "publicKey";
    public static final String DEVICE_PRIVATE_KEY = "privateKey";

    private DeviceTable() {
    }

    public static DeviceTable getInstance() {
        if (instance == null)
            instance = new DeviceTable();
        return instance;
    }

    @Override
    public void createTable() throws SQLException {
        String sql = "create table if not exists " + DEVICE_TABLE + " (" +
                DEVICE_SERIAL + " int primary key, " +
                DEVICE_PUBLIC_KEY + " varchar not null, " +
                DEVICE_PRIVATE_KEY + " varchar not null);";
        try (Statement statement = PostgresConnectable.getConnection().createStatement()) {
            statement.execute(sql);
        }
    }

    @Override
    public void insert(Device instance) throws SQLException {
        String sql = "insert into " + DEVICE_TABLE + " values (?, ?, ?);";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setInt(1, instance.getDeviceSerial());
            statement.setString(2, instance.getPublicKey());
            statement.setString(3, instance.getPrivateKey());
            statement.execute();
        }
    }

    @Override
    public Device read(Device partialInstance) throws SQLException {
        String sql = "select * from " + DEVICE_TABLE + " where " + DEVICE_SERIAL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setInt(1, partialInstance.getDeviceSerial());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next())
                    return Device.getInstance(resultSet.getInt(resultSet.findColumn(DEVICE_SERIAL)),
                            resultSet.getString(resultSet.findColumn(DEVICE_PRIVATE_KEY)),
                            resultSet.getString(resultSet.findColumn(DEVICE_PUBLIC_KEY)));
                return null;
            }
        }
    }

    @Override
    public void update(Device instance) throws SQLException {
        String sql = "update " + DEVICE_TABLE + " set " + DEVICE_PUBLIC_KEY + "=? where " + DEVICE_SERIAL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setString(1, instance.getPublicKey());
            statement.setInt(2, instance.getDeviceSerial());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Device instance) throws SQLException {
        String sql = "delete from " + DEVICE_TABLE + " where " + DEVICE_SERIAL + "=?;";
        try (PreparedStatement statement = PostgresConnectable.getConnection().prepareStatement(sql)) {
            statement.setInt(1, instance.getDeviceSerial());
            statement.execute();
        }
    }
}
