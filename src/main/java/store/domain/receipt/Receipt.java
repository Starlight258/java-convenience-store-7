package store.domain.receipt;

import java.util.List;
import store.domain.order.OrderResult;
import store.domain.product.Product;
import store.domain.promotion.PromotionResult;

public class Receipt {

    private final List<PurchaseResult> purchaseResults;
    private final List<GiftResult> giftResults;
    private final int totalPurchaseAmount;
    private final int totalGiftDiscountAmount;
    private final int totalMembershipDiscountAmount;
    private final int totalPayAmount;

    public Receipt(final List<PurchaseResult> purchaseResults, final List<GiftResult> giftResults,
                   final int totalMembershipDiscountAmount) {
        this.purchaseResults = purchaseResults;
        this.giftResults = giftResults;
        this.totalPurchaseAmount = calculateTotalPurchaseAmount(purchaseResults);
        this.totalGiftDiscountAmount = calculateTotalGiftDiscountAmount(giftResults);
        this.totalMembershipDiscountAmount = totalMembershipDiscountAmount;
        this.totalPayAmount = totalPurchaseAmount - totalGiftDiscountAmount - totalMembershipDiscountAmount;
    }

    public static Receipt from(final List<OrderResult> orderResults, final boolean wantMembership) {
        List<PurchaseResult> purchaseResults = makePurchaseResults(orderResults);
        List<GiftResult> giftResults = makeGiftResults(orderResults);
        int totalMembershipDiscountAmount = addMembershipDiscountAmount(orderResults, wantMembership);
        return new Receipt(purchaseResults, giftResults, totalMembershipDiscountAmount);
    }

    private static List<PurchaseResult> makePurchaseResults(final List<OrderResult> orderResults) {
        return orderResults.stream()
                .map(OrderResult::purchaseResult)
                .toList();
    }

    private static List<GiftResult> makeGiftResults(final List<OrderResult> orderResults) {
        return orderResults.stream()
                .map(OrderResult::giftResult)
                .filter(result -> result.quantity > 0)
                .toList();
    }

    private static int addMembershipDiscountAmount(final List<OrderResult> orderResults, final boolean wantMembership) {
        if (wantMembership) {
            return orderResults.stream()
                    .mapToInt(OrderResult::membershipDiscountAmount)
                    .sum();
        }
        return 0;
    }

    public record PurchaseResult(String productName, int quantity, int price) {
        public static PurchaseResult of(Product product, PromotionResult promotionResult) {
            return new PurchaseResult(product.getName(), promotionResult.totalQuantity(), product.getPrice());
        }
    }

    public record GiftResult(String productName, int quantity, int price) {
        public static GiftResult of(Product product, PromotionResult promotionResult) {
            return new GiftResult(product.getName(), promotionResult.giftQuantity(), product.getPrice());
        }
    }

    private int calculateTotalPurchaseAmount(final List<PurchaseResult> purchaseResults) {
        return purchaseResults.stream()
                .mapToInt(result -> result.price * result.quantity)
                .sum();
    }

    private int calculateTotalGiftDiscountAmount(final List<GiftResult> giftResults) {
        return giftResults.stream()
                .mapToInt(result -> result.price * result.quantity)
                .sum();
    }

    public List<PurchaseResult> getPurchaseResults() {
        return purchaseResults;
    }

    public List<GiftResult> getGiftResults() {
        return giftResults;
    }

    public int getTotalPurchaseQuantity() {
        return purchaseResults.stream()
                .mapToInt(PurchaseResult::quantity)
                .sum();
    }

    public int getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public int getTotalGiftDiscountAmount() {
        return totalGiftDiscountAmount;
    }

    public int getTotalMembershipDiscountAmount() {
        return totalMembershipDiscountAmount;
    }

    public int getTotalPayAmount() {
        return totalPayAmount;
    }
}
