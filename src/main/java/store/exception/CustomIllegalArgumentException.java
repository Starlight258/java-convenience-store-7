package store.exception;

import static store.exception.ErrorMessage.ERROR_PREFIX;

public class CustomIllegalArgumentException extends IllegalArgumentException {

    public CustomIllegalArgumentException(final String message) {
        super(ERROR_PREFIX.getMessage() + message);
    }
}
