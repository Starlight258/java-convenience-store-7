package store.domain.promotion;

public class MembershipCalculator {

    private static final int MAX_MEMBERSHIP_PRICE = 8000;
    private static final int MEMBERSHIP_DISCOUNT_RATE = 30;

    public int calculate(final int productPrice, final PromotionResult promotionResult) {
        return calculateMembershipPrice(productPrice * getRegularPriceQuantity(promotionResult));
    }

    private int calculateMembershipPrice(final int price) {
        return Math.min(MAX_MEMBERSHIP_PRICE, price * MEMBERSHIP_DISCOUNT_RATE / 100);
    }

    public int getRegularPriceQuantity(final PromotionResult promotionResult) {
        if (promotionResult.isRegularOnlyPurchase()) {
            return promotionResult.totalQuantity();
        }
        return promotionResult.regularPriceQuantity();
    }
}
