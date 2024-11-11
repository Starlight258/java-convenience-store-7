package store.view;

import java.math.BigDecimal;
import store.support.StoreFormatter;

public class OutputView {

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

    public void showMessage(final String message) {
        System.out.println(message);
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
        System.out.printf("총구매액\t\t%d\t%,.0f", quantity, totalPurchaseValue);
        System.out.println();
    }

    public void showPromotionDiscountPrice(final BigDecimal promotionDiscountPrice) {
        int length = String.format(",.0f", promotionDiscountPrice).length();
        System.out.printf("행사할인\t\t\t-%,.0f", promotionDiscountPrice);
        System.out.println();
    }

    public void showMemberShipDiscountPrice(final BigDecimal memberShipDiscountPrice) {
        int length = String.format(",.0f", memberShipDiscountPrice).length();
        System.out.printf("멤버십할인\t\t\t-%,.0f", memberShipDiscountPrice);
        System.out.println();
    }

    public void showMoneyToPay(final BigDecimal priceToPay) {
        int length = String.format(",.0f", priceToPay).length();
        System.out.printf("내실돈\t\t\t%,.0f", priceToPay);
        System.out.println();
    }

    public void showBonusProduct(final String name, final int quantity) {
        System.out.printf("%s\t\t\t%d", name, quantity);
        System.out.println();
    }

    public void showInventory(final String name, final int quantity, final BigDecimal totalPrice) {
        System.out.printf("%s\t\t\t%d\t%,.0f%n", name, quantity, totalPrice);
        System.out.println();
    }

    public void showCommentOfInventory(int length) {
        System.out.println(System.lineSeparator() + STORE_HEADER);
        System.out.printf("상품명\t\t수량\t금액");
        System.out.println();
    }
}
