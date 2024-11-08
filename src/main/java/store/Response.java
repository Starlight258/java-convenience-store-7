package store;

import java.math.BigDecimal;

public record Response(ResponseStatus status, int bonusQuantity, int noPromotionQuantity, int canGetMoreQuantity,
                       BigDecimal totalPrice) {

    public static Response buyWithNoPromotion(BigDecimal totalPrice) { // 그냥 구매한 금액
        return new Response(ResponseStatus.BUY_WITH_NO_PROMOTION, 0, 0, 0, totalPrice);
    }

    public static Response buyWithPromotion(final int bonusQuantity) {
        return new Response(ResponseStatus.BUY_WITH_PROMOTION, bonusQuantity, 0, 0, BigDecimal.ZERO);
    }

    public static Response outOfStock(final int bonusQuantity, final int noPromotionQuantity) {
        return new Response(ResponseStatus.OUT_OF_STOCK, bonusQuantity, noPromotionQuantity, 0, BigDecimal.ZERO);
    }

    public static Response canGetMoreQuantity(final int bonusQuantity, final int canGetMoreQuantity) {
        return new Response(ResponseStatus.CAN_GET_BONUS, bonusQuantity, 0, canGetMoreQuantity, BigDecimal.ZERO);
    }
}
