package server.exception;

public class ProtocolException extends Exception {
    // 400 Bad Request
    public static final int EMAIL_NOT_VALIDATED = 400001;
    public static final int PASSWORD_NOT_VALIDATED = 400002;
    public static final int DEVICE_SERIAL_NOT_VALIDATED = 400003;
    public static final int PRIVATE_KEY_NOT_VALIDATED = 400004;
    public static final int PUBLIC_KEY_NOT_VALIDATED = 400005;
    public static final int TIME_INSTANT_NOT_VALIDATED = 400006;
    public static final int DATA_KEY_NOT_VALIDATED = 400007;
    public static final int DATA_VALUE_NOT_VALIDATED = 400008;
    public static final int COMMAND_FORMAT_NOT_VALIDATED = 400009;
    public static final int COMMAND_PROPERTY_NOT_VALIDATED = 400010;
    public static final int DEVICE_SERIAL_PROPERTY_NOT_VALIDATED = 400011;
    public static final int PUBLIC_KEY_PROPERTY_NOT_VALIDATED = 400012;
    public static final int PRIVATE_KEY_PROPERTY_NOT_VALIDATED = 400013;

    // 401 Unauthorized
    public static final int PASSWORD_MISMATCH = 401001;
    public static final int PRIVATE_KEY_MISMATCH = 401002;
    public static final int PUBLIC_KEY_MISMATCH = 401003;
    public static final int TOKEN_EXPIRED = 401004;
    public static final int TOKEN_NOT_VALIDATED = 401005;

    // 403 Forbidden
    public static final int DUPLICATE_USER = 403001;

    // 404 Not Found
    public static final int USER_NOT_FOUND = 404001;
    public static final int DEVICE_NOT_FOUND = 404002;

    // 500 Internal Server Error
    public static final int INTERNAL_DATABASE_ERROR = 500001;

    // 501 Not Implemented
    public static final int NOT_IMPLEMENTED = 501001;

    private final int code;

    public ProtocolException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        return code / 1000;
    }

    public String getStringCode() {
        return String.valueOf(code);
    }
}
