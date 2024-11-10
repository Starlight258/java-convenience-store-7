package store.response;

import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.quantity.Quantity;

public record Response(ResponseStatus status, Quantity bonusQuantity, Quantity noPromotionQuantity,
                       Quantity canGetMoreQuantity,
                       Inventory inventory, Inventories sameProductInventories) {

    public static Response buyWithNoPromotion(final Inventory inventory) { // 그냥 구매한 금액
        return new Response(ResponseStatus.BUY_WITH_NO_PROMOTION, Quantity.zero(), Quantity.zero(), Quantity.zero(),
                inventory, null);
    }

    public static Response buyWithPromotion(final Quantity bonusQuantity, final Inventory inventory) {
        return new Response(ResponseStatus.BUY_WITH_PROMOTION, bonusQuantity, Quantity.zero(), Quantity.zero(),
                inventory, null);
    }

    public static Response outOfStock(final Quantity bonusQuantity, final Quantity noPromotionQuantity,
                                      final Inventory inventory, final Inventories sameProductInventories) {
        return new Response(ResponseStatus.OUT_OF_STOCK, bonusQuantity, noPromotionQuantity, Quantity.zero(),
                inventory, sameProductInventories);
    }

    public static Response canGetMoreQuantity(final Quantity bonusQuantity, final Quantity freeQuantity,
                                              final Inventory inventory) {

        return new Response(ResponseStatus.CAN_GET_BONUS, bonusQuantity, Quantity.zero(), freeQuantity,
                inventory, null);
    }
}
