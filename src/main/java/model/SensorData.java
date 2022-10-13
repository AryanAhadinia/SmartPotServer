package model;

import com.google.gson.Gson;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import db.timeseries.InfluxManager;
import server.exception.ProtocolException;

import java.time.Instant;

@Measurement(name = "SensorData")
public class SensorData {
    @Column(timestamp = true)
    private final Instant time;
    @Column(tag = true)
    private final int deviceSerial;
    @Column(tag = true)
    private final String sensorName;
    @Column
    private final double value;

    private SensorData(Instant time, int deviceSerial, String sensorName, double value) {
        this.time = time;
        this.deviceSerial = deviceSerial;
        this.sensorName = sensorName;
        this.value = value;
    }

    public static SensorData getInstance(Instant time, int deviceSerial, String sensorName, double value) {
        return new SensorData(time, deviceSerial, sensorName, value);
    }

    public String getSensorName() {
        return sensorName;
    }

    public double getValue() {
        return value;
    }

    public Instant getTime() {
        return time;
    }

    public void save() {
        InfluxManager.insertData(this);
    }

    public String getJSON() throws ProtocolException {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
