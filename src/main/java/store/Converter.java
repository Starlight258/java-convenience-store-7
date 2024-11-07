package store;

public class Converter {

    public static int convertToInteger(final String input) {
        validateNotNullOrBlank(input);
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("[ERROR] 정수로 변환할 수 없습니다.");
        }
    }

    private static void validateNotNullOrBlank(final String input) {
        if (input == null) {
            throw new IllegalArgumentException("null일 수 없습니다.");
        }
        if (input.isBlank()) {
            throw new IllegalArgumentException("비어있을 수 없습니다");
        }
    }
}
