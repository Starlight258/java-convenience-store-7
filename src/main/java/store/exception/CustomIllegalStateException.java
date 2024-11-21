package store.exception;

import static store.exception.ErrorMessage.ERROR_PREFIX;

public class CustomIllegalStateException extends IllegalArgumentException {

    public CustomIllegalStateException(final String message) {
        super(ERROR_PREFIX.getMessage() + message);
    }
}
