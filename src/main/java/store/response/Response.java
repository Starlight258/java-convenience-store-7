package store.response;

import store.domain.inventory.Inventory;

public record Response(ResponseStatus status, int bonusQuantity, int noPromotionQuantity, int canGetMoreQuantity,
                       Inventory inventory) {

    public static Response buyWithNoPromotion(final Inventory inventory) { // 그냥 구매한 금액
        return new Response(ResponseStatus.BUY_WITH_NO_PROMOTION, 0, 0, 0, inventory);
    }

    public static Response buyWithPromotion(final int bonusQuantity, final Inventory inventory) {
        return new Response(ResponseStatus.BUY_WITH_PROMOTION, bonusQuantity, 0, 0, inventory);
    }

    public static Response outOfStock(final int bonusQuantity, final int noPromotionQuantity,
                                      final Inventory inventory) {
        return new Response(ResponseStatus.OUT_OF_STOCK, bonusQuantity, noPromotionQuantity, 0,
                inventory);
    }

    public static Response canGetMoreQuantity(final int bonusQuantity, final int canGetMoreQuantity,
                                              final Inventory inventory) {

        return new Response(ResponseStatus.CAN_GET_BONUS, bonusQuantity, 0, canGetMoreQuantity,
                inventory);
    }
}
