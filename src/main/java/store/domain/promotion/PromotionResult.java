package store.domain.promotion;

import java.util.Objects;

/*
    regularPriceQuantity : 정가 결제 수량 -> 안내, Y일 경우 멤버십 비용 계산에 포함
    totalQuantity : 구매 총 수량 -> 영수증
    additionalBenefitQuantity : 추가 혜택 수량 -> 안내 (Y일 경우 총 수량과 증정 수량에 포함시키기)
    giftQuantity : 증정 수량 -> 영수증
 */
public record PromotionResult(PurchaseType purchaseType, int regularPriceQuantity, int totalQuantity,
                              int additionalBenefitQuantity, int giftQuantity) {
    // 멤버십 비용 계산 : totalQuantity
    public static PromotionResult makeRegularPurchaseResult(int totalQuantity) {
        return new PromotionResult(PurchaseType.REGULAR_ONLY, 0, totalQuantity, 0, 0);
    }

    // 멤버십 비용 계산 : Y일 경우 regularPriceQuantity만
    public static PromotionResult makeMixedPurchaseResult(int regularPriceQuantity, int totalQuantity,
                                                          int additionalBenefitQuantity, int giftQuantity) {
        return new PromotionResult(PurchaseType.MIXED, regularPriceQuantity, totalQuantity, additionalBenefitQuantity,
                giftQuantity);
    }

    // 멤버십 비용 계산 : X
    public static PromotionResult makePromotionPurchaseResult(int totalQuantity, int additionalBenefitQuantity,
                                                              int giftQuantity) {
        return new PromotionResult(PurchaseType.PROMOTIONAL_ONLY, 0, totalQuantity, additionalBenefitQuantity,
                giftQuantity);
    }

    // 일부 수량에 대해 정가 결제 안내 필요
    public boolean askRegularPayment() {
        return regularPriceQuantity > 0;
    }

    // 추가 혜택 안내
    public boolean askBenefit() {
        return additionalBenefitQuantity > 0;
    }
}
