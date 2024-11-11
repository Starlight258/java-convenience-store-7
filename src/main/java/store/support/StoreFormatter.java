package store.support;

import store.domain.inventory.Inventory;

public class StoreFormatter {

    private static final String NO_STOCK = "재고 없음";
    private static final String QUANTITY_UNIT = "개";
    private static final String NULL = "null";

    public String makeInventoryMessage(final Inventory inventory) {
        String quantityText = makeQuantityText(inventory.getQuantity().getQuantity());
        String promotionText = makePromotionText(inventory.getPromotionName());
        return String.format("- %s %,.0f원 %s%s",
                        inventory.getProductName(), inventory.getProduct().getPrice().getPrice(), quantityText, promotionText)
                .trim();
    }

    public String format(String word, int formatSize) {
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

    private String makeQuantityText(final int quantity) {
        if (quantity == 0) {
            return NO_STOCK;
        }
        return quantity + QUANTITY_UNIT;
    }

    private String makePromotionText(final String promotionName) {
        if (promotionName.equals(NULL)) {
            return "";
        }
        return " " + promotionName;
    }
}
