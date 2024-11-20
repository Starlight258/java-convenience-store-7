package store.util;

import store.domain.inventory.Inventory;

public class StoreFormatter {

    private static final String INVENTORY_MESSAGE_FORMAT = "- %s %,.0f원 %s%s";
    private static final String STRING_FORMAT = "%%-%ds";

    private static final String NO_STOCK = "재고 없음";
    private static final String QUANTITY_UNIT = "개";
    private static final String NULL = "null";
    private static final String EMPTY = "";
    private static final String SPACE = " ";

    private static final char KOREAN_FIRST = '가';
    private static final char KOREAN_LAST = '힣';

    public static String makeInventoryMessage(final Inventory inventory) {
        String quantityText = makeQuantityText(inventory.getQuantity().getQuantity());
        String promotionText = makePromotionText(inventory.getPromotionName());
        return String.format(INVENTORY_MESSAGE_FORMAT,
                inventory.getProductName(),
                inventory.getProduct().getPrice().getPrice(),
                quantityText,
                promotionText).trim();
    }

    public static String format(String word, int formatSize) {
        String formatter = String.format(STRING_FORMAT, formatSize - countKoreanCharacters(word));
        return String.format(formatter, word);
    }

    private static int countKoreanCharacters(String text) {
        return (int) text.chars()
                .filter(StoreFormatter::isKoreanCharacter)
                .count();
    }

    private static boolean isKoreanCharacter(int ch) {
        return ch >= KOREAN_FIRST && ch <= KOREAN_LAST;
    }

    private static String makeQuantityText(final int quantity) {
        if (quantity == 0) {
            return NO_STOCK;
        }
        return quantity + QUANTITY_UNIT;
    }

    private static String makePromotionText(final String promotionName) {
        if (NULL.equals(promotionName)) {
            return EMPTY;
        }
        return SPACE + promotionName;
    }
}
