package store.util;

import store.exception.CustomIllegalArgumentException;

public class Converter {

    private static final String CANNOT_CONVERT_TO_INTEGER = "정수로 변환할 수 없습니다.";

    private Converter() {
    }

    public static int convertToInteger(final String input) {
        InputValidator.validateNotNullOrBlank(input);
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            throw new CustomIllegalArgumentException(CANNOT_CONVERT_TO_INTEGER);
        }
    }
}
