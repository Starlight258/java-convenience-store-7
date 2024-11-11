package store.view;

import java.math.BigDecimal;
import java.util.Map.Entry;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;
import store.support.StoreFormatter;

public class OutputView {

    private static final int FORMAT_SIZE = 12;
    private static final String STORE_HEADER = "==============W 편의점================";

    private final StoreFormatter storeFormatter;

    public OutputView(final StoreFormatter storeFormatter) {
        this.storeFormatter = storeFormatter;
    }

    public void showStartMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.");
        System.out.println();
    }

    public void showExceptionMessage(final String message) {
        System.out.println(message);
    }

    public void showPromotionDiscount(final String productName, final int noPromotionQuantityOfResponse) {
        System.out.printf(System.lineSeparator() + "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)"
                + System.lineSeparator(), productName, noPromotionQuantityOfResponse);
    }

    public void showFreeQuantity(final String productName, final int canGetMoreQuantity) {
        System.out.printf(System.lineSeparator() + "현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)"
                + System.lineSeparator(), productName, canGetMoreQuantity);
    }

    public void showBlankLine() {
        System.out.println();
    }

    public void showAdditionalPurchase() {
        System.out.println(System.lineSeparator() + "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
    }

    public void showCommentOfPurchase() {
        System.out.println(System.lineSeparator() + "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
    }

    public void showCommentOfMemberShip() {
        System.out.println(System.lineSeparator() + "멤버십 할인을 받으시겠습니까? (Y/N)");
    }

    public void showReceiptStartMark() {
        System.out.println("====================================");
    }

    public void showBonus() {
        System.out.println("=============증\t정===============");
    }

    public void showTotalPrice(final Integer quantity, final BigDecimal totalPurchaseValue) {
        System.out.printf(
                storeFormatter.format("총구매액", FORMAT_SIZE) + " \t%d \t%,.0f"
                        + System.lineSeparator(),
                quantity, totalPurchaseValue);
    }

    public void showPromotionDiscountPrice(final BigDecimal promotionDiscountPrice) {
        System.out.printf(storeFormatter.format("행사할인", FORMAT_SIZE) + " \t\t-%,.0f\n",
                promotionDiscountPrice);
    }

    public void showMemberShipDiscountPrice(final BigDecimal memberShipDiscountPrice) {
        System.out.printf(storeFormatter.format("멤버십할인", FORMAT_SIZE) + " \t\t-%,.0f\n",
                memberShipDiscountPrice);
    }

    public void showMoneyToPay(final BigDecimal priceToPay) {
        System.out.printf(storeFormatter.format("내실돈", FORMAT_SIZE) + " \t\t %,.0f\n", priceToPay);
    }

    public void showBonusProduct(final String name, final int quantity) {
        System.out.printf(storeFormatter.format(name, FORMAT_SIZE) + "\t%d \t" + System.lineSeparator(), quantity);
    }

    public void showInventory(final String name, final int quantity, final BigDecimal totalPrice) {
        System.out.printf(storeFormatter.format(name, FORMAT_SIZE) + "\t%d \t%,.0f\n", quantity, totalPrice);
    }

    public void showCommentOfInventory() {
        System.out.println(System.lineSeparator() + STORE_HEADER);
        System.out.printf("상품명\t\t수량\t금액");
        System.out.println();
    }

    public void showPurchaseProducts(final Receipt receipt) {
        showCommentOfInventory();
        for (Entry<Product, Quantity> entry : receipt.getPurchasedProducts().entrySet()) {
            showEachProduct(entry);
        }
    }

    public void showReceipt(final Receipt receipt, final Price membershipPrice) {
        showReceiptStartMark();
        Entry<Quantity, Price> totalPurchases = receipt.getTotalPurchase();
        Price priceToPay = receipt.getPriceToPay(totalPurchases.getValue(), membershipPrice);
        Price totalPurchasePrice = totalPurchases.getValue();
        showTotalPrice(totalPurchases.getKey().getQuantity(), totalPurchasePrice.getPrice());
        showPromotionDiscountPrice(receipt.getPromotionDiscountPrice().getPrice());
        showMemberShipDiscountPrice(membershipPrice.getPrice());
        showMoneyToPay(priceToPay.getPrice());
    }

    public void showBonusProducts(final Receipt receipt) {
        showBonus();
        for (Entry<Product, Quantity> entry : receipt.getBonusProducts().entrySet()) {
            Product product = entry.getKey();
            String name = product.getName();
            int quantity = entry.getValue().getQuantity();
            showBonusProduct(name, quantity);
        }
    }

    public void showMessage(final String message) {
        System.out.println(message);
    }

    private void showEachProduct(final Entry<Product, Quantity> entry) {
        Product product = entry.getKey();
        String name = product.getName();
        Quantity quantity = entry.getValue();
        Price totalPrice = entry.getKey().getPrice().multiply(BigDecimal.valueOf(quantity.getQuantity()));
        showInventory(name, quantity.getQuantity(), totalPrice.getPrice());
    }
}
