import db.sql.manager.PostgresConnection;
import db.timeseries.InfluxClient;
import db.timeseries.InfluxManager;
import server.Server;
import server.auth.AuthManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    private static final String DB_PROPERTIES = "db.properties";

    public static void main(String[] args) {
        try (InputStream propertiesStream = ClassLoader.getSystemResourceAsStream(DB_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(propertiesStream);

            int port = Integer.parseInt(properties.getProperty("PORT"));
            String postgresURL = properties.getProperty("PSQL_URL");
            String postgresUsername = properties.getProperty("PSQL_USER");
            String postgresPassword = properties.getProperty("PSQL_PASS");
            String influxURL = properties.getProperty("INFLUX_URL");
            String influxToken = properties.getProperty("INFLUX_TOKEN");
            String secret = properties.getProperty("SECRET");

            AuthManager.initial(secret);

            PostgresConnection.createInstance(postgresURL, postgresUsername, postgresPassword);
            InfluxClient.createInstance(influxURL, influxToken);

            InfluxManager.getData(100000001);

            Server server = Server.getInstance(port);
            server.start();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
