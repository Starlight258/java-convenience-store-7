package store.support;

import java.math.BigDecimal;

public class StoreFormatter {

    private static final String NO_STOCK = "재고 없음";
    private static final String QUANTITY_UNIT = "개";
    private static final String NULL = "null";

    private final int formatSize;

    public StoreFormatter(final int formatSize) {
        this.formatSize = formatSize;
    }

    public String format(String word) {
        String formatter = String.format("%%-%ds", formatSize - getKoreanCount(word));
        return String.format(formatter, word);
    }

    public int getCount(String word) {
        return getKoreanCount(word);
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

    public String makeInventoryMessage(final int quantity, final String promotionName,
                                       final String productName, final BigDecimal productPrice) {
        String quantityText = makeQuantityText(quantity);
        String promotionText = makePromotionText(promotionName);
        return String.format("- %s %,d원 %s%s",
                productName, productPrice.intValue(), quantityText, promotionText).trim();
    }

    private String makeQuantityText(final int quantity) {
        return quantity == 0 ? NO_STOCK : quantity + QUANTITY_UNIT;
    }

    private String makePromotionText(final String promotionName) {
        return promotionName.equals(NULL) ? "" : " " + promotionName;
    }
}
