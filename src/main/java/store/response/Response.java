package store.response;

import store.domain.inventory.Inventory;
import store.domain.quantity.Quantity;

public record Response(ResponseStatus status, Quantity bonusQuantity, Quantity noPromotionQuantity,
                       Quantity canGetMoreQuantity, Inventory inventory) {

    public static Response purchaseWithNoPromotion(final Inventory inventory) {
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

    public static Response canGetMoreQuantity(final Quantity bonusQuantity, final Quantity freeQuantity,
                                              final Inventory inventory) {

        return new Response(ResponseStatus.CAN_GET_BONUS, bonusQuantity, Quantity.zero(), freeQuantity, inventory);
    }
}
