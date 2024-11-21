package store.view;

import java.math.BigDecimal;
import java.util.Map.Entry;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.util.StoreFormatter;

public class OutputView {

    private static final int FORMAT_SIZE = 12;
    private static final String DOUBLE_TAB = "\t\t";
    private static final String NEW_LINE = System.lineSeparator();

    private static final String STORE_HEADER = "==============W 편의점================";
    private static final String RECEIPT_HEADER = "====================================";
    private static final String BONUS_HEADER = "=============증\t정===============";

    private static final String WELCOME_MESSAGE = "안녕하세요. W편의점입니다.";
    private static final String CURRENT_INVENTORY_MESSAGE = "현재 보유하고 있는 상품입니다.";
    private static final String PROMOTION_DISCOUNT_FORMAT =
            NEW_LINE + "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)" + NEW_LINE;
    private static final String FREE_QUANTITY_FORMAT =
            NEW_LINE + "현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)" + NEW_LINE;
    private static final String ADDITIONAL_PURCHASE_MESSAGE = NEW_LINE + "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";
    private static final String PURCHASE_GUIDE_MESSAGE = NEW_LINE + "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private static final String MEMBERSHIP_QUESTION = NEW_LINE + "멤버십 할인을 받으시겠습니까? (Y/N)";

    private static final String TOTAL_PURCHASE_LABEL = "총구매액";
    private static final String PROMOTION_DISCOUNT_LABEL = "행사할인";
    private static final String MEMBERSHIP_DISCOUNT_LABEL = "멤버십할인";
    private static final String FINAL_PRICE_LABEL = "내실돈";
    private static final String INVENTORY_HEADER = "상품명\t\t\t수량\t\t금액";

    private static final String PRICE_FORMAT = "%,.0f";
    private static final String TOTAL_PRICE_FORMAT = "\t\t%d\t\t" + PRICE_FORMAT + NEW_LINE;
    private static final String DISCOUNT_PRICE_FORMAT =
            DOUBLE_TAB + DOUBLE_TAB + "-" + PRICE_FORMAT + DOUBLE_TAB + NEW_LINE;
    private static final String FINAL_PRICE_FORMAT = DOUBLE_TAB + DOUBLE_TAB + " " + PRICE_FORMAT + NEW_LINE;
    private static final String INVENTORY_FORMAT = "\t\t%d\t\t%,.0f" + NEW_LINE;
    private static final String BONUS_FORMAT = "\t\t%d\t\t" + NEW_LINE;

    public OutputView() {
    }

    public void showStartMessage() {
        System.out.println(WELCOME_MESSAGE);
        System.out.println(CURRENT_INVENTORY_MESSAGE);
        System.out.println();
    }

    public void showExceptionMessage(final String message) {
        System.out.println(message);
    }

    public void showPromotionDiscount(final String productName, final int noPromotionQuantityOfResponse) {
        System.out.printf(PROMOTION_DISCOUNT_FORMAT, productName, noPromotionQuantityOfResponse);
    }

    public void showFreeQuantity(final String productName, final int canGetMoreQuantity) {
        System.out.printf(FREE_QUANTITY_FORMAT, productName, canGetMoreQuantity);
    }

    public void showBlankLine() {
        System.out.println();
    }

    public void askAdditionalPurchase() {
        System.out.println(ADDITIONAL_PURCHASE_MESSAGE);
    }

    public void showCommentOfPurchase() {
        System.out.println(PURCHASE_GUIDE_MESSAGE);
    }

    public void showCommentOfMemberShip() {
        System.out.println(MEMBERSHIP_QUESTION);
    }

    public void showReceiptStartMark() {
        System.out.println(RECEIPT_HEADER);
    }

    public void showBonus() {
        System.out.println(BONUS_HEADER);
    }

    public void showTotalPrice(final Integer quantity, final BigDecimal totalPurchaseValue) {
        System.out.printf(
                StoreFormatter.format(TOTAL_PURCHASE_LABEL, FORMAT_SIZE) + TOTAL_PRICE_FORMAT,
                quantity, totalPurchaseValue);
    }

    public void showPromotionDiscountPrice(final BigDecimal promotionDiscountPrice) {
        System.out.printf(StoreFormatter.format(PROMOTION_DISCOUNT_LABEL, FORMAT_SIZE) + DISCOUNT_PRICE_FORMAT,
                promotionDiscountPrice);
    }

    public void showMemberShipDiscountPrice(final BigDecimal memberShipDiscountPrice) {
        System.out.printf(StoreFormatter.format(MEMBERSHIP_DISCOUNT_LABEL, FORMAT_SIZE) + DISCOUNT_PRICE_FORMAT,
                memberShipDiscountPrice);
    }

    public void showMoneyToPay(final BigDecimal priceToPay) {
        System.out.printf(StoreFormatter.format(FINAL_PRICE_LABEL, FORMAT_SIZE) + FINAL_PRICE_FORMAT, priceToPay);
    }

    public void showBonusProduct(final String name, final int quantity) {
        System.out.printf(StoreFormatter.format(name, FORMAT_SIZE) + BONUS_FORMAT, quantity);
    }

    public void showInventory(final String name, final int quantity, final BigDecimal totalPrice) {
        System.out.printf(StoreFormatter.format(name, FORMAT_SIZE) + INVENTORY_FORMAT, quantity, totalPrice);
    }

    public void showCommentOfInventory() {
        System.out.println(NEW_LINE + STORE_HEADER);
        System.out.println(INVENTORY_HEADER);
    }

    public void showMessage(final String message) {
        System.out.println(message);
    }

    public void showResults(Receipt receipt, Price membershipPrice) {
        showPurchaseProducts(receipt);
        showBonusProducts(receipt);
        showReceipt(receipt, membershipPrice);
    }

    private void showPurchaseProducts(final Receipt receipt) {
        showCommentOfInventory();
        for (Entry<Product, Quantity> entry : receipt.getPurchasedProducts().entrySet()) {
            showEachProduct(entry);
        }
    }

    private void showReceipt(final Receipt receipt, final Price membershipPrice) {
        showReceiptStartMark();
        Entry<Quantity, Price> totalPurchases = receipt.getTotalPurchase();
        Price priceToPay = receipt.getPriceToPay(totalPurchases.getValue(), membershipPrice);
        Price totalPurchasePrice = totalPurchases.getValue();
        showTotalPrice(totalPurchases.getKey().getQuantity(), totalPurchasePrice.getPrice());
        showPromotionDiscountPrice(receipt.getPromotionDiscountPrice().getPrice());
        showMemberShipDiscountPrice(membershipPrice.getPrice());
        showMoneyToPay(priceToPay.getPrice());
    }

    private void showBonusProducts(final Receipt receipt) {
        showBonus();
        for (Entry<Product, Quantity> entry : receipt.getBonusProducts().entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue().getQuantity();
            showBonusProduct(name, quantity);
        }
    }

    private void showEachProduct(final Entry<Product, Quantity> entry) {
        Product product = entry.getKey();
        String name = product.getName();
        Quantity quantity = entry.getValue();
        Price totalPrice = entry.getKey().getPrice().multiply(BigDecimal.valueOf(quantity.getQuantity()));
        showInventory(name, quantity.getQuantity(), totalPrice.getPrice());
    }
}
