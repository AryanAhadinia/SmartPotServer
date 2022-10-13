package db.timeseries;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import model.SensorData;
import server.exception.ProtocolException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfluxManager {
    private final static String BUCKET = "Sensor";
    private final static String ORG = "Sharif";

    private static InfluxDBClient getClient() {
        return InfluxClient.getInstance().getClient();
    }

    public static void insertData(SensorData sensorData) {
        WriteApiBlocking writeApi = getClient().getWriteApiBlocking();
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.S, sensorData);
    }

    public static List<SensorData> getData(int deviceSerial) {
        String query = "from(bucket: \"Sensor\")\n" +
                "  |> range(start: 0)\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"SensorData\")\n" +
                "  |> sort()\n" +
                "  |> yield(name: \"mean\")";
        List<FluxTable> tables = getClient().getQueryApi().query(query, ORG);

        List<SensorData> data = new ArrayList<>();
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                SensorData sensorData = SensorData.getInstance(record.getTime(), deviceSerial,
                        (String) record.getValues().get("sensorName"), (Double) record.getValue());
                data.add(sensorData);
            }
        }
        return data;
    }
}
