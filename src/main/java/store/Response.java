package store;

public record Response(RESPONSE_STATUS status, int bonusQuantity, int noPromotionQuantity, int canGetMoreQuantity) {

    public static Response buyWithNoPromotion() {
        return new Response(RESPONSE_STATUS.BUY_WITH_NO_PROMOTION, 0, 0, 0);
    }

    public static Response buyWithPromotion(final int bonusQuantity) {
        return new Response(RESPONSE_STATUS.BUY, bonusQuantity, 0, 0);
    }

    public static Response outOfStock(final int bonusQuantity, final int noPromotionQuantity) {
        return new Response(RESPONSE_STATUS.OUT_OF_STOCK, bonusQuantity, noPromotionQuantity, 0);
    }

    public static Response canGetMoreQuantity(final int bonusQuantity, final int canGetMoreQuantity) {
        return new Response(RESPONSE_STATUS.CAN_GET_BONUS, bonusQuantity, 0, canGetMoreQuantity);
    }
}
