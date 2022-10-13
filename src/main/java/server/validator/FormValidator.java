package server.validator;

import io.javalin.http.Context;
import model.Device;
import org.jetbrains.annotations.NotNull;
import server.Server;
import server.exception.ProtocolException;

import java.time.Instant;

public class FormValidator implements Validator {

    @NotNull
    private final Context context;

    public FormValidator(@NotNull Context context) {
        this.context = context;
    }

    @Override
    public String getUserEmail() {
        return context.formParamAsClass(Server.USER_EMAIL, String.class)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.EMAIL_NOT_VALIDATED));
    }

    @Override
    public String getUserPassword() {
        return context.formParamAsClass(Server.USER_PASSWORD, String.class)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.PASSWORD_NOT_VALIDATED));
    }

    @Override
    public int getDeviceSerial() {
        return context.formParamAsClass(Server.DEVICE_SERIAL, Integer.class)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.DEVICE_SERIAL_NOT_VALIDATED));
    }

    @Override
    public String getPublicKey() {
        return context.formParamAsClass(Server.PUBLIC_KEY, String.class)
                .check(k -> Device.KEY_MIN_LEN <= k.length() && k.length() <= Device.KEY_MAX_LEN, Server.PUBLIC_KEY)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.PUBLIC_KEY_NOT_VALIDATED));
    }

    @Override
    public String getPrivateKey() {
        return context.formParamAsClass(Server.PRIVATE_KEY, String.class)
                .check(k -> Device.KEY_MIN_LEN <= k.length() && k.length() <= Device.KEY_MAX_LEN, Server.PRIVATE_KEY)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.PRIVATE_KEY_NOT_VALIDATED));
    }

    @Override
    public Instant getTimeInstant() {
        return context.formParamAsClass(Server.TIME_INSTANT, Instant.class)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.TIME_INSTANT_NOT_VALIDATED));
    }

    @Override
    public String getDataKey() {
        return context.formParamAsClass(Server.DATA_KEY, String.class)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.DATA_KEY_NOT_VALIDATED));
    }

    @Override
    public double getDataValue() {
        return context.formParamAsClass(Server.DATA_VALUE, Double.class)
                .getOrThrow(stringMap -> new ProtocolException(ProtocolException.DATA_VALUE_NOT_VALIDATED));
    }
}
