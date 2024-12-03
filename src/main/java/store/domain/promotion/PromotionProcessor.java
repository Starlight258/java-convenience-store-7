package store.domain.promotion;

import java.time.LocalDate;
import store.domain.product.Product;
import store.domain.product.stock.ProductStock;

// 프로모션을 고려하여 상품의 재고 관리
public class PromotionProcessor {

    private final ProductStock productStock;

    public PromotionProcessor(final ProductStock productStock) {
        this.productStock = productStock;
    }

    public PromotionResult processOrder(int purchaseQuantity, LocalDate now) {
        Product product = productStock.getProduct();
        // 유효한 프로모션이 존재하고, 프로모션 수량이 존재할 경우
        if (hasValidPromotion(product, now) && !productStock.doesNotExistPromotionQuantity()) {
            return purchasePromotion(product.getPromotion(), purchaseQuantity);
        }
        return purchaseRegular(purchaseQuantity);
    }

    private boolean hasValidPromotion(final Product product, LocalDate now) {
        return product.hasValidPromotion(now);
    }

    private PromotionResult purchasePromotion(final Promotion promotion, final int purchaseQuantity) {
        // 재고 확인
        validateStock(purchaseQuantity);
        // 프로모션 재고로 모두 구매하지 못할 경우 프로모션 적용 + 정가 결제 수량을 안내
        if (needRegularPricePayment(purchaseQuantity)) {
            return processPartialPromotionPurchase(promotion, purchaseQuantity);
        }
        return calculatePromotionBenefit(promotion, purchaseQuantity);
    }

    private PromotionResult calculatePromotionBenefit(final Promotion promotion, final int purchaseQuantity) {
        int giftQuantity = calculateGiftQuantity(promotion, purchaseQuantity);
        // 프로모션 적용이 가능한 상품에 대해 해당 수량보다 적게 가져온 경우 추가 혜택 안내
        if (checkAdditionalPromotionBenefit(promotion, purchaseQuantity)) {
            return PromotionResult.makePromotionPurchaseResult(purchaseQuantity, promotion.getGetQuantity(),
                    giftQuantity);
        }
        // 할인 적용
        return purchaseWithPromotion(purchaseQuantity, giftQuantity);
    }

    private PromotionResult purchaseWithPromotion(final int purchaseQuantity, final int giftQuantity) {
        productStock.subtractPromotionQuantity(purchaseQuantity);
        return PromotionResult.makePromotionPurchaseResult(purchaseQuantity, 0, giftQuantity);
    }

    private boolean checkAdditionalPromotionBenefit(final Promotion promotion, final int purchaseQuantity) {
        // 추가 혜택 안내시 구매 후 남은 재고 수량이 getQuantity 이상이어야한다.
        boolean isBenefitQuantity = (purchaseQuantity % promotion.getUnitQuantity()) == promotion.getBuyQuantity();
        boolean isAvailable = productStock.getPromotionQuantity() - purchaseQuantity >= promotion.getGetQuantity();
        return isBenefitQuantity && isAvailable;
    }

    private PromotionResult processPartialPromotionPurchase(final Promotion promotion, final int purchaseQuantity) {
        int promotionQuantity = productStock.getPromotionQuantity();
        int unitQuantity = promotion.getUnitQuantity();
        int regularPriceQuantity = calculateRegularPriceQuantity(purchaseQuantity, promotionQuantity, unitQuantity);
        int giftQuantity = calculateGiftQuantity(promotion, purchaseQuantity);
        return PromotionResult.makeMixedPurchaseResult(regularPriceQuantity, purchaseQuantity, 0, giftQuantity);
    }

    private int calculateGiftQuantity(final Promotion promotion, final int purchaseQuantity) {
        // 프로모션 재고 내에서 증정 수량을 계산
        int quantity = Math.min(purchaseQuantity, productStock.getPromotionQuantity());
        return quantity / promotion.getUnitQuantity() * promotion.getGetQuantity();
    }

    private boolean needRegularPricePayment(final int purchaseQuantity) {
        return productStock.cannotPurchaseWithinPromotion(purchaseQuantity);
    }

    private void validateStock(final int purchaseQuantity) {
        productStock.checkTotalStock(purchaseQuantity);
    }

    private int calculateRegularPriceQuantity(int purchaseQuantity, int promotionQuantity, int unitQuantity) {
        return purchaseQuantity - (promotionQuantity / unitQuantity * unitQuantity);
    }

    private PromotionResult purchaseRegular(final int purchaseQuantity) {
        productStock.subtractRegularQuantity(purchaseQuantity);
        return PromotionResult.makeRegularPurchaseResult(purchaseQuantity);
    }

    public PromotionResult processWithRegularPayment(final PromotionResult promotionResult) {
        int purchaseQuantity = promotionResult.totalQuantity();
        int promotionQuantity = productStock.getPromotionQuantity();
        productStock.subtractPromotionQuantity(promotionQuantity);
        productStock.subtractRegularQuantity(purchaseQuantity - promotionQuantity);
        return promotionResult;
    }

    public PromotionResult processOnlyPromotionPayment(final PromotionResult promotionResult) {
        int totalQuantity = promotionResult.totalQuantity();
        int regularPriceQuantity = promotionResult.regularPriceQuantity();
        int purchaseQuantity = totalQuantity - regularPriceQuantity;
        productStock.subtractPromotionQuantity(purchaseQuantity);
        return PromotionResult.makePromotionPurchaseResult(purchaseQuantity,
                promotionResult.additionalBenefitQuantity(), promotionResult.giftQuantity());
    }

    public PromotionResult processBenefitOption(final PromotionResult promotionResult) {
        int totalQuantity = promotionResult.totalQuantity();
        int additionalBenefitQuantity = promotionResult.additionalBenefitQuantity();
        int purchaseQuantity = totalQuantity + additionalBenefitQuantity;
        productStock.subtractPromotionQuantity(purchaseQuantity);
        return PromotionResult.makePromotionPurchaseResult(purchaseQuantity, 0,
                promotionResult.giftQuantity() + additionalBenefitQuantity);
    }

    public PromotionResult processNoBenefitOption(final PromotionResult promotionResult) {
        int totalQuantity = promotionResult.totalQuantity();
        productStock.subtractPromotionQuantity(totalQuantity);
        return PromotionResult.makePromotionPurchaseResult(totalQuantity, 0, promotionResult.giftQuantity());
    }
}
