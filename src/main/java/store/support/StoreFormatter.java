package store.support;

public class StoreFormatter {

    public static final String BLANK = " ";
    private final int formatSize;

    public StoreFormatter(final int formatSize) {
        this.formatSize = formatSize;
    }

    public String format(String word) {
        String formatter = String.format("%%-%ds", formatSize - getKoreanCount(word));
        return String.format(formatter, word);
    }

    private int getKoreanCount(String text) {
        int cnt = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) >= '가' && text.charAt(i) <= '힣') {
                cnt++;
            }
        }
        return cnt;
    }

    public String getBlank(int length) {
        return BLANK.repeat(Math.max(0, length));
    }
}
