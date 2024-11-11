package store.util;

import static store.exception.ExceptionMessages.NOT_NULL_ARGUMENT;
import static store.exception.ExceptionMessages.NOT_NULL_BLANK;
import static store.exception.ExceptionMessages.NOT_NULL_EMPTY;

import java.math.BigDecimal;
import java.util.List;

public class InputValidator {

    public static void validateNotNullOrBlank(final String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(NOT_NULL_BLANK.getMessageWithPrefix());
        }
    }

    public static void validateNotNullOrEmpty(final List<?> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException(NOT_NULL_EMPTY.getMessageWithPrefix());
        }
    }

    public static void validateNotNull(final BigDecimal input) {
        if (input == null) {
            throw new IllegalArgumentException(NOT_NULL_ARGUMENT.getMessageWithPrefix());
        }
    }
}
