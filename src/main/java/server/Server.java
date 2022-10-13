package server;

import com.google.gson.Gson;
import db.sql.tables.SubscriptionTable;
import db.timeseries.InfluxManager;
import io.javalin.Javalin;
import io.javalin.core.validation.JavalinValidation;
import model.Device;
import model.SensorData;
import model.User;
import server.auth.AuthManager;
import server.exception.ProtocolException;
import server.live.LiveServer;
import server.validator.FormValidator;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Server {
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String DEVICE_SERIAL = "deviceSerial";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String TIME_INSTANT = "timeInstant";
    public static final String DATA_KEY = "dataKey";
    public static final String DATA_VALUE = "dataValue";
    public static final String TOKEN = "token";

    private final int port;
    private final LiveServer liveServer;

    private Server(int port) {
        this.port = port;
        this.liveServer = new LiveServer();
    }

    public static Server getInstance(int port) {
        return new Server(port);
    }

    public User authenticateUser(String token) throws ProtocolException, SQLException {
        if (token == null || "".equals(token)) {
            throw new ProtocolException(ProtocolException.USER_NOT_FOUND);
        }
        String email = AuthManager.getInstance().verifyToken(token);
        User user = User.getUserByEmail(email);
        if (user == null) {
            throw new ProtocolException(ProtocolException.USER_NOT_FOUND);
        }
        return user;
    }

    public Double getLastValue(List<SensorData> sensorData, String key) {
        long maxTime = 0;
        double value = 0;
        for (int i = 0; i < sensorData.size(); i++) {
            if (sensorData.get(i).getSensorName().equals(key) && sensorData.get(i).getTime().toEpochMilli() > maxTime) {
                maxTime =  sensorData.get(i).getTime().toEpochMilli();
                value = sensorData.get(i).getValue();
            }
        }
        return value;
    }

    public String getJSONToSend(User user) throws SQLException {
        List<Device> devices = SubscriptionTable.getInstance().getRelated1(user);
        HashMap<Integer, HashMap<String, Double>> jsonMap = new HashMap<>();
        for (Device device : devices) {
            List<SensorData> sensorData = InfluxManager.getData(device.getDeviceSerial());
            HashMap<String, Double> deviceData = new HashMap<>();
            deviceData.put("Hum", getLastValue(sensorData, "Hum"));
            deviceData.put("Temp", getLastValue(sensorData, "Temp"));
            deviceData.put("Lev", getLastValue(sensorData, "Lev"));
            jsonMap.put(device.getDeviceSerial(), deviceData);
        }
        Gson gson = new Gson();
        return gson.toJson(jsonMap);
    }

    public void start() {
        Javalin app = Javalin.create().start(port);
        JavalinValidation.register(Instant.class, s -> Instant.ofEpochMilli(Long.parseLong(s)));

        app.get("/api/ping", ctx -> ctx.result("pong"));
        app.ws("/api/ping_ws", ws -> {
            ws.onConnect(ctx -> ctx.send("pong"));
            ws.onMessage(ctx -> ctx.send(ctx.message()));
            ws.onError(ctx -> {
            });
            ws.onClose(ctx -> {
            });
        });
        app.patch("/api/public_key_update", ctx -> {
            FormValidator validator = new FormValidator(ctx);
            Device device = Device.getDeviceBySerial(validator.getDeviceSerial());
            if (device == null) {
                throw new ProtocolException(ProtocolException.DEVICE_NOT_FOUND);
            }
            if (!device.getPrivateKey().equals(validator.getPrivateKey())) {
                throw new ProtocolException(ProtocolException.PRIVATE_KEY_MISMATCH);
            }
            device.setPublicKey(validator.getPublicKey());
        });
        app.post("/api/post", ctx -> {
            FormValidator validator = new FormValidator(ctx);
            Device device = Device.getDeviceBySerial(validator.getDeviceSerial());
            if (device == null) {
                throw new ProtocolException(ProtocolException.DEVICE_NOT_FOUND);
            }
            if (!device.getPrivateKey().equals(validator.getPrivateKey())) {
                throw new ProtocolException(ProtocolException.PRIVATE_KEY_MISMATCH);
            }
            SensorData sensorData = SensorData.getInstance(validator.getTimeInstant(), device.getDeviceSerial(),
                    validator.getDataKey(), validator.getDataValue());
            sensorData.save();
            for (User user : SubscriptionTable.getInstance().getRelated2(device)) {
                this.liveServer.broadcast(getJSONToSend(user), user);
            }
        });
        app.post("/api/sign_up", ctx -> {
            FormValidator validator = new FormValidator(ctx);
            String email = validator.getUserEmail();
            String password = validator.getUserPassword();
            if (User.getUserByEmail(email) != null) {
                throw new ProtocolException(ProtocolException.DUPLICATE_USER);
            }
            User newUser = User.getInstance(email, User.hashPassword(password));
            newUser.save();
            String token = AuthManager.getInstance().getToken(newUser.getEmail());
            ctx.cookie(TOKEN, token);
            ctx.header(TOKEN, token);
            ctx.result(token);
        });
        app.post("/api/sign_in", ctx -> {
            FormValidator validator = new FormValidator(ctx);
            String email = validator.getUserEmail();
            String password = validator.getUserPassword();
            User user = User.getUserByEmail(email);
            if (user == null) {
                throw new ProtocolException(ProtocolException.USER_NOT_FOUND);
            }
            if (!user.getPasswordHash().equals(User.hashPassword(password))) {
                throw new ProtocolException(ProtocolException.PASSWORD_MISMATCH);
            }
            String token = AuthManager.getInstance().getToken(user.getEmail());
            ctx.cookie(TOKEN, token);
            ctx.header(TOKEN, token);
            ctx.result(token);
        });
        app.delete("/api/sign_out", ctx -> ctx.removeCookie(TOKEN));
        app.get("/api/echo", ctx -> {
            User user = authenticateUser(ctx.cookie(TOKEN));
            String token = AuthManager.getInstance().getToken(user.getEmail());
            ctx.result(token);
        });
        app.post("/api/subscribe", ctx -> {
            User user = authenticateUser(ctx.cookie(TOKEN));
            FormValidator validator = new FormValidator(ctx);
            Device device = Device.getDeviceBySerial(validator.getDeviceSerial());
            if (device == null) {
                throw new ProtocolException(ProtocolException.DEVICE_NOT_FOUND);
            }
            if (!device.getPublicKey().equals(validator.getPublicKey())) {
                throw new ProtocolException(ProtocolException.PUBLIC_KEY_MISMATCH);
            }
            SubscriptionTable.getInstance().insert(user, device);
            this.liveServer.broadcast(getJSONToSend(user), user);
        });
        app.delete("/api/unsubscribe", ctx -> {
            User user = authenticateUser(ctx.cookie(TOKEN));
            FormValidator validator = new FormValidator(ctx);
            Device device = Device.getDeviceBySerial(validator.getDeviceSerial());
            if (device == null) {
                throw new ProtocolException(ProtocolException.DEVICE_NOT_FOUND);
            }
            SubscriptionTable.getInstance().delete(user, device);
            this.liveServer.broadcast(getJSONToSend(user), user);
        });
        app.ws("/api/live", ws -> {
            ws.onConnect(ctx -> {
                // String token = ctx.pathParam(TOKEN);
                // User user = authenticateUser(token);
                User user = User.getUserByEmail("aryanahadinia24@gmail.com");
                liveServer.subscribe(user, ctx);
                ctx.send(getJSONToSend(user));
            });
            ws.onMessage(ctx -> {
            });
            ws.onError(ctx -> {
            });
            ws.onClose(ctx -> {
            });
        });
        app.exception(SQLException.class, ((e, ctx) -> {
            ProtocolException exception = new ProtocolException(ProtocolException.INTERNAL_DATABASE_ERROR);
            ctx.result(exception.getStringCode()).status(exception.getStatus());
        }));
        app.exception(ProtocolException.class, ((e, ctx) -> ctx.result(e.getStringCode()).status(e.getStatus())));
    }
}
