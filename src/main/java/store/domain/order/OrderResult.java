package store.domain.order;

import store.domain.product.stock.ProductStock;
import store.domain.promotion.PromotionResult;
import store.domain.receipt.Receipt.GiftResult;
import store.domain.receipt.Receipt.PurchaseResult;

public record OrderResult(PurchaseResult purchaseResult, GiftResult giftResult, int membershipDiscountAmount) {

    public static OrderResult of(ProductStock productStock, PromotionResult promotionResult,
                                 int membershipDiscountAmount) {
        PurchaseResult purchaseResult = PurchaseResult.of(productStock.getProduct(), promotionResult);
        GiftResult giftResult = GiftResult.of(productStock.getProduct(), promotionResult);
        return new OrderResult(purchaseResult, giftResult, membershipDiscountAmount);
    }
}
