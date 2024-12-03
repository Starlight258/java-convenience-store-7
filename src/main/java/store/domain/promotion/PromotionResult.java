package store.domain.promotion;

/*
    <영수증>
    totalQuantity : 구매 총 수량
    giftQuantity : 증정 수량
    <안내>
    regularPriceQuantity : 구매 총 수량 중 정가 결제 안내 수량 -> Y일 경우 멤버십 비용 계산에 포함, N일 경우 구매 총 수량에서 제외
    additionalBenefitQuantity : 추가 혜택 안내 수량 -> Y일 경우 구매 총 수량과 증정 수량에 포함, N일 경우 작업 X
 */
public record PromotionResult(PurchaseType purchaseType, int totalQuantity, int giftQuantity, int regularPriceQuantity,
                              int additionalBenefitQuantity) {
    // 멤버십 비용 계산 : totalQuantity
    public static PromotionResult makeRegularPurchaseResult(int totalQuantity) {
        return new PromotionResult(PurchaseType.REGULAR_ONLY, totalQuantity, 0, 0, 0);
    }

    // 멤버십 비용 계산 : Y일 경우 regularPriceQuantity만
    public static PromotionResult makeMixedPurchaseResult(int totalQuantity, int giftQuantity, int regularPriceQuantity,
                                                          int additionalBenefitQuantity) {
        return new PromotionResult(PurchaseType.MIXED, totalQuantity, giftQuantity, regularPriceQuantity,
                additionalBenefitQuantity
        );
    }

    // 멤버십 비용 계산 : X
    public static PromotionResult makePromotionPurchaseResult(int totalQuantity, int giftQuantity,
                                                              int additionalBenefitQuantity) {
        return new PromotionResult(PurchaseType.PROMOTIONAL_ONLY, totalQuantity, giftQuantity, 0,
                additionalBenefitQuantity
        );
    }

    // 일부 수량에 대해 정가 결제 안내 필요
    public boolean askRegularPayment() {
        return regularPriceQuantity > 0;
    }

    // 추가 혜택 안내
    public boolean askBenefit() {
        return additionalBenefitQuantity > 0;
    }

    public boolean isRegularOnlyPurchase() {
        return this.purchaseType == PurchaseType.REGULAR_ONLY;
    }
}
