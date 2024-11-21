package store.util;

import static store.exception.ErrorMessage.NULL;
import static store.exception.ErrorMessage.NULL_OR_BLANK;
import static store.exception.ErrorMessage.NULL_OR_EMPTY;

import java.util.List;
import store.exception.CustomIllegalArgumentException;

public class InputValidator {

    private InputValidator() {
    }

    public static void validateNotNullOrBlank(final String input) {
        if (input == null || input.isBlank()) {
            throw new CustomIllegalArgumentException(NULL_OR_BLANK.getMessage());
        }
    }

    public static void validateNotNullOrEmpty(final List<?> input) {
        if (input == null || input.isEmpty()) {
            throw new CustomIllegalArgumentException(NULL_OR_EMPTY.getMessage());
        }
    }

    public static <T> void validateNotNull(final T input) {
        if (input == null) {
            throw new CustomIllegalArgumentException(NULL.getMessage());
        }
    }
}
