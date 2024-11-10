package store.exception;

public class ExceptionMessage {

    private final String message;

    public ExceptionMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return ExceptionMessages.ERROR_PREFIX.getErrorMessage() + message;
    }
}
