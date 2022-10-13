package model;

import db.sql.tables.DeviceTable;

import java.sql.SQLException;

public class Device {
    public static final int KEY_MIN_LEN = 8;
    public static final int KEY_MAX_LEN = 64;

    private final int deviceSerial;
    private final String privateKey;
    private String publicKey;

    private Device(int deviceSerial, String privateKey, String publicKey) {
        this.deviceSerial = deviceSerial;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public static Device getInstance(int deviceSerial, String privateKey, String publicKey) {
        return new Device(deviceSerial, privateKey, publicKey);
    }

    public static Device getAndSaveInstance(int deviceSerial, String privateKey, String publicKey) {
        return new Device(deviceSerial, privateKey, publicKey);
    }

    public int getDeviceSerial() {
        return deviceSerial;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) throws SQLException {
        this.publicKey = publicKey;
        DeviceTable.getInstance().update(this);
    }

    public static Device getDeviceBySerial(int deviceSerial) throws SQLException {
        return DeviceTable.getInstance().read(new Device(deviceSerial, null, null));
    }

    public void delete() throws SQLException {
        DeviceTable.getInstance().delete(this);
    }
}
