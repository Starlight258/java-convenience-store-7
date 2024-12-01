package store.exception;

public class ErrorPrefix {

    private static final String PREFIX = "[ERROR] ";

    public static String format(String message) {
        return PREFIX + message;
    }
}
