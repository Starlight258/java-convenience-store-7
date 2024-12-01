package store.exception;

public class CustomIllegalArgumentException extends IllegalArgumentException {

    public CustomIllegalArgumentException(final ErrorMessage message) {
        super(ErrorPrefix.format(message.getMessage()));
    }
}
