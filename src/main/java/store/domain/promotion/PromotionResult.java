package store.domain.promotion;

/*
    regularPriceQuantity : 정가 결제 수량 -> 안내, 멤버십 비용 계산
    totalQuantity : 구매 총 수량 -> 영수증
    additionalBenefitQuantity : 추가 혜택 수량 -> 안내 (Y일 경우 총 수량과 증정 수량에 포함)
    giftQuantity : 증정 수량 -> 영수증
 */
public record PromotionResult(PurchaseType purchaseType, int regularPriceQuantity,
                              int totalQuantity, int additionalBenefitQuantity, int giftQuantity) {
    public static PromotionResult makeRegularPurchaseResult(int regularPriceQuantity) {
        return new PromotionResult(PurchaseType.REGULAR_ONLY, regularPriceQuantity, regularPriceQuantity, 0, 0);
    }

    public static PromotionResult makeMixedPurchaseResult(int regularPriceQuantity, int totalQuantity,
                                                          int additionalBenefitQuantity, int giftQuantity) {
        return new PromotionResult(PurchaseType.MIXED, regularPriceQuantity, totalQuantity, additionalBenefitQuantity,
                giftQuantity);
    }

    public static PromotionResult makePromotionPurchaseResult(int totalQuantity, int additionalBenefitQuantity,
                                                              int giftQuantity) {
        return new PromotionResult(PurchaseType.PROMOTIONAL_ONLY, 0, totalQuantity, additionalBenefitQuantity,
                giftQuantity);
    }

    // 일부 수량에 대해 정가 결제 안내 필요
    public boolean needRegularPaymentConfirmation() {
        return this.purchaseType == PurchaseType.MIXED;
    }

    // 추가 혜택 안내
    public boolean askBenefit() {
        return additionalBenefitQuantity > 0;
    }
}
