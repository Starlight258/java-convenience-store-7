package store;

public record Response(RESPONSE_STATUS status, int bonusQuantity, int noPromotionQuantity) {

    public static Response buyWithNoPromotion(final RESPONSE_STATUS status) {
        return new Response(status, 0, 0);
    }

    public static Response buyWithPromotion(final RESPONSE_STATUS status, final int bonusQuantity) {
        return new Response(status, bonusQuantity, 0);
    }

    public static Response outOfStock(final int noPromotionQuantity) {
        return new Response(RESPONSE_STATUS.OUT_OF_STOCK, 0, noPromotionQuantity);
    }
}
