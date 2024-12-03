package store.view;

import static store.domain.product.stock.StockStatus.NOT_EXIST;
import static store.view.ResultFormatter.formatKorean;

import java.util.Map.Entry;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.product.stock.ProductStock;
import store.domain.product.stock.QuantityEnum;
import store.domain.promotion.Promotion;
import store.domain.receipt.Receipt;
import store.domain.receipt.Receipt.GiftResult;
import store.domain.receipt.Receipt.PurchaseResult;

public class OutputView {

    private static final String LINE = System.lineSeparator();
    private static final String WELCOME = "안녕하세요. W편의점입니다." + LINE + "현재 보유하고 있는 상품입니다.";
    private static final String REQUEST_PRODUCT = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private static final String REQUEST_REGULAR_PAYMENT = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
    private static final String REQUEST_BENEFIT = "현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)";
    private static final String REQUEST_MEMBERSHIP = "멤버십 할인을 받으시겠습니까? (Y/N)";
    private static final String PROMOTION_FORMAT = "- %s %,d원 ";
    private static final String TITLE = "==============W 편의점================";
    private static final String PURCHASE_TITLE_FORMAT = "상품명\t\t\t\t수량\t\t금액";
    private static final String PURCHASE_FORMAT = "\t\t\t%d\t\t%,d";
    private static final String GIFT_TITLE = "=============증\t\t정===============";
    private static final String GIFT_FORMAT = "\t\t\t%d";
    private static final String TOTAL_TILE = "====================================";
    private static final String TOTAL_PURCHASE_AMOUNT_FORMAT = "총구매액\t\t\t\t%d\t\t%,d";
    private static final String PROMOTION_DISCOUNT_FORMAT = "행사할인\t\t\t\t\t\t-%,d";
    private static final String MEMBERSHIP_DISCOUNT_FORMAT = "멤버십할인\t\t\t\t\t\t-%,d";
    private static final String MONEY_TO_PAY_DISCOUNT_FORMAT = "내실돈\t\t\t\t\t\t %,d";
    private static final String REQUEST_RETRY = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";

    public void requestRetry() {
        showln(LINE + REQUEST_RETRY);
    }

    private void showPurchaseResult(final Receipt receipt) {
        showln(LINE + TITLE);
        showln(PURCHASE_TITLE_FORMAT);
        for (PurchaseResult result : receipt.getPurchaseResults()) {
            showln(format(formatKorean(result.productName(), 12) + PURCHASE_FORMAT, result.quantity(),
                    result.quantity() * result.price()));
        }
    }

    private void showGiftResult(final Receipt receipt) {
        showln(GIFT_TITLE);
        for (GiftResult result : receipt.getGiftResults()) {
            showln(format(formatKorean(result.productName(), 12) + GIFT_FORMAT, result.quantity()));
        }
    }

    public void requestMembership() {
        showln(LINE + REQUEST_MEMBERSHIP);
    }

    public void showWelcome() {
        showln(WELCOME + LINE);
    }

    public void requestOrder() {
        showln(LINE + REQUEST_PRODUCT);
    }

    public void requestRegularPayment(final String name, final int quantity) {
        showln(LINE + format(REQUEST_REGULAR_PAYMENT, name, quantity));
    }

    public void showInventory(final Inventory inventory) {
        for (Entry<String, ProductStock> entry : inventory.getInventory().entrySet()) {
            ProductStock productStock = entry.getValue();
            Product product = productStock.getProduct();
            Promotion promotion = product.getPromotion();
            String name = entry.getKey();
            int price = product.getPrice();
            int promotionQuantity = productStock.getPromotionQuantity();
            int regularQuantity = productStock.getRegularQuantity();
            if (promotionQuantity != NOT_EXIST.getValue()) {
                String quantityName = QuantityEnum.findByStock(promotionQuantity);
                showln(String.format(PROMOTION_FORMAT, name, price) + quantityName + " " + promotion.getName());
            }
            String quantityName = QuantityEnum.findByStock(regularQuantity);
            showln(String.format(PROMOTION_FORMAT, name, price) + quantityName);
        }
    }

    public void showException(final RuntimeException e) {
        System.out.println(e.getMessage());
    }

    public void requestBenefit(final String name) {
        showln(format(LINE + REQUEST_BENEFIT, name));
    }

    public void showReceipt(final Receipt receipt) {
        showPurchaseResult(receipt);
        showGiftResult(receipt);
        showTotalAmountResult(receipt);
    }

    private void showTotalAmountResult(final Receipt receipt) {
        showln(TOTAL_TILE);
        showln(format(TOTAL_PURCHASE_AMOUNT_FORMAT, receipt.getTotalPurchaseQuantity(),
                receipt.getTotalPurchaseAmount()));
        showln(format(PROMOTION_DISCOUNT_FORMAT, receipt.getTotalGiftDiscountAmount()));
        showln(format(MEMBERSHIP_DISCOUNT_FORMAT, receipt.getTotalMembershipDiscountAmount()));
        showln(format(MONEY_TO_PAY_DISCOUNT_FORMAT, receipt.getTotalPayAmount()));
    }

    private String format(String format, Object... args) {
        return String.format(format, args);
    }

    public void showLine() {
        showln("");
    }

    private void showln(String message) {
        System.out.println(message);
    }
}
