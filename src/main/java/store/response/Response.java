package store.response;

import store.domain.inventory.Inventory;
import store.domain.quantity.Quantity;

public record Response(ResponseStatus status, Quantity bonusQuantity, Quantity noPromotionQuantity,
                       Quantity canGetMoreQuantity,
                       Inventory inventory) {

    public static Response buyWithNoPromotion(final Inventory inventory) { // 그냥 구매한 금액
        return new Response(ResponseStatus.BUY_WITH_NO_PROMOTION, Quantity.zero(), Quantity.zero(), Quantity.zero(),
                inventory);
    }

    public static Response buyWithPromotion(final Quantity bonusQuantity, final Inventory inventory) {
        return new Response(ResponseStatus.BUY_WITH_PROMOTION, bonusQuantity, Quantity.zero(), Quantity.zero(),
                inventory);
    }

    public static Response outOfStock(final Quantity bonusQuantity, final Quantity noPromotionQuantity,
                                      final Inventory inventory) {
        return new Response(ResponseStatus.OUT_OF_STOCK, bonusQuantity, noPromotionQuantity, Quantity.zero(),
                inventory);
    }

    public static Response canGetMoreQuantity(final Inventory inventory) {

        return new Response(ResponseStatus.CAN_GET_BONUS, Quantity.one(), Quantity.zero(), Quantity.one(),
                inventory);
    }
}
