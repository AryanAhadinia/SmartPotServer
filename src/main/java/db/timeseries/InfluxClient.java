package db.timeseries;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

public class InfluxClient {
    private static InfluxClient instance;

    private final String url;
    private final String token;

    private InfluxDBClient client;

    public InfluxClient(String url, String token) {
        this.url = url;
        this.token = token;
    }

    public static void createInstance(String url, String token) {
        instance = new InfluxClient(url, token);
        instance.connect();
    }

    public static InfluxClient getInstance() {
        return instance;
    }

    private void connect() {
        client = InfluxDBClientFactory.create(url, token.toCharArray());
    }

    public InfluxDBClient getClient() {
        return client;
    }
}
