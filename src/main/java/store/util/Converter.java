package store.util;

import store.exception.ExceptionMessage;
import store.exception.ExceptionMessages;

public class Converter {

    public static final ExceptionMessage CANNOT_CONVERT_TO_INTEGER = new ExceptionMessage("정수로 변환할 수 없습니다.");

    public static int convertToInteger(final String input) {
        validateNotNullOrBlank(input);
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(CANNOT_CONVERT_TO_INTEGER.getMessage());
        }
    }

    private static void validateNotNullOrBlank(final String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessages.NOT_NULL_BLANK.getErrorMessage());
        }
    }
}
