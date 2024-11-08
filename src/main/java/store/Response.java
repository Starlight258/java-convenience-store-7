package store;

import java.math.BigDecimal;

public record Response(ResponseStatus status, int bonusQuantity, int noPromotionQuantity, int canGetMoreQuantity,
                       BigDecimal totalPrice, Inventory inventory) {

    public static Response buyWithNoPromotion(BigDecimal totalPrice, final Inventory inventory) { // 그냥 구매한 금액
        return new Response(ResponseStatus.BUY_WITH_NO_PROMOTION, 0, 0, 0, totalPrice, inventory);
    }

    public static Response buyWithPromotion(final int bonusQuantity, final Inventory inventory) {
        return new Response(ResponseStatus.BUY_WITH_PROMOTION, bonusQuantity, 0, 0, BigDecimal.ZERO, inventory);
    }

    public static Response outOfStock(final int bonusQuantity, final int noPromotionQuantity,
                                      final Inventory inventory) {
        return new Response(ResponseStatus.OUT_OF_STOCK, bonusQuantity, noPromotionQuantity, 0, BigDecimal.ZERO,
                inventory);
    }

    public static Response canGetMoreQuantity(final int bonusQuantity, final int canGetMoreQuantity,
                                              final Inventory inventory) {

        return new Response(ResponseStatus.CAN_GET_BONUS, bonusQuantity, 0, canGetMoreQuantity, BigDecimal.ZERO,
                inventory);
    }
}
