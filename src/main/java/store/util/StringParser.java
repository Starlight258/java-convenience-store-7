package store.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    /**
     * 정규식 패턴으로 문자열을 파싱하여 매칭되는 그룹들의 리스트를 반환
     */
    public static List<String> findMatchingGroups(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        List<String> groups = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                groups.add(matcher.group(i));
            }
        }
        return Collections.unmodifiableList(groups);
    }
}
