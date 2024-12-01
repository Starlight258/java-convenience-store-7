package store.util;

import java.util.Arrays;
import java.util.List;
import store.exception.CustomIllegalArgumentException;
import store.exception.ErrorMessage;

public class StringParser {

    private static final int CONTAINS_EMPTY = -1;
    private static final String BLANK = "";

    private StringParser() {
    }

    public static int parseToInteger(final String input, final ErrorMessage message) {
        try {
            return Integer.parseInt(input.strip());
        } catch (NumberFormatException exception) {
            throw new CustomIllegalArgumentException(message);
        }
    }

    public static String removePattern(final String value, final String regex) {
        return value.replaceAll(regex, BLANK);
    }

    /**
     * 구분자로 문자열을 분리하여 리스트로 반환
     */
    public static List<String> parseByDelimiter(String input, String delimiter) {
        return Arrays.stream(input.split(delimiter, CONTAINS_EMPTY))
                .map(String::strip)
                .toList();
    }
}
