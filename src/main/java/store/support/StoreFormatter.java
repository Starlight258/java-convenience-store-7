package store.support;

import java.math.BigDecimal;

public class StoreFormatter {

    public static final String BLANK = " ";
    public static final String NULL = "null";
    public static final String QUANTITY_UNIT = "개 ";
    public static final String NO_STOCK = "재고 없음 ";

    private final int formatSize;

    public StoreFormatter(final int formatSize) {
        this.formatSize = formatSize;
    }

    public String format(String word) {
        String formatter = String.format("%%-%ds", formatSize - getKoreanCount(word));
        return String.format(formatter, word);
    }

    public String makeInventoryMessage(final int quantity, final String promotionName, final String productName,
                                       final BigDecimal productPrice) {
        String quanityText = quantity + QUANTITY_UNIT;
        if (quantity == 0) {
            quanityText = NO_STOCK;
        }
        String promotionNameText = promotionName;
        if (promotionName.equals(NULL)) {
            promotionNameText = "";
        }
        return String.format("- %s" + " %,.0f원 %s%s\n", productName, productPrice, quanityText, promotionNameText);
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
