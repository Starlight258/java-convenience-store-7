package store.util;

import java.util.regex.Pattern;
import store.exception.CustomIllegalArgumentException;
import store.exception.ErrorMessage;

public class InputValidator {

    private InputValidator() {
    }

    public static void validateNotNullOrBlank(final String input, final ErrorMessage message) {
        if (input == null || input.isBlank()) {
            throw new CustomIllegalArgumentException(message);
        }
    }

    public static void isInvalidPattern(String input, Pattern pattern, ErrorMessage errorMessage) {
        if (!pattern.matcher(input).matches()) {
            throw new CustomIllegalArgumentException(errorMessage);
        }
    }
}
