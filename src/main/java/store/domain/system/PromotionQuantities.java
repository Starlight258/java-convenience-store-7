package store.domain.system;

import store.domain.promotion.Promotion;
import store.domain.quantity.Quantity;

public record PromotionQuantities(
        Quantity purchaseQuantity,
        Quantity bonusQuantity,
        Quantity totalQuantity
) {
    public static PromotionQuantities from(Promotion promotion) {
        return new PromotionQuantities(
                promotion.getPurchaseQuantity(),
                promotion.getBonusQuantity(),
                promotion.getPurchaseQuantity().add(promotion.getBonusQuantity())
        );
    }
}
