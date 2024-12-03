package store.view;

public class ResultFormatter {

    private static final String STRING_FORMAT = "%%-%ds";

    public static String formatKorean(String word, int formatSize) {
        String formatter = String.format(STRING_FORMAT, formatSize - countKoreanCharacters(word));
        return String.format(formatter, word);
    }

    private static int countKoreanCharacters(String text) {
        return (int) text.chars()
                .filter(ResultFormatter::isKoreanCharacter)
                .count();
    }

    private static boolean isKoreanCharacter(int ch) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
        return block == Character.UnicodeBlock.HANGUL_SYLLABLES
                || block == Character.UnicodeBlock.HANGUL_JAMO
                || block == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
    }
}
