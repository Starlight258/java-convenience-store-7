package store.view;

import static store.domain.product.stock.StockStatus.NOT_EXIST;

import java.util.Map.Entry;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.product.stock.ProductStock;
import store.domain.product.stock.QuantityEnum;
import store.domain.promotion.Promotion;

public class OutputView {

    private static final String LINE = System.lineSeparator();
    private static final String WELCOME = "안녕하세요. W편의점입니다." + LINE + "현재 보유하고 있는 상품입니다.";
    private static final String REQUEST_PRODUCT = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private static final String REQUEST_REGULAR_PAYMENT = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
    private static final String REQUEST_BENEFIT = "현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)";
    private static final String REQUEST_MEMBERSHIP = "멤버십 할인을 받으시겠습니까? (Y/N)";

    public void requestMembership() {
        showln(REQUEST_MEMBERSHIP);
    }

    public void showWelcome() {
        showln(WELCOME + LINE);
    }

    public void requestOrder() {
        showln(LINE + REQUEST_PRODUCT);
    }

    public void requestRegularPayment(final String name, final int quantity) {
        showln(format(REQUEST_REGULAR_PAYMENT, name, quantity));
    }

    private void showln(String message) {
        System.out.println(message);
    }

    public void showInventory(final Inventory inventory) {
        String promotionFormat = "- %s %,d원 ";
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
                showln(String.format(promotionFormat, name, price) + quantityName + " " + promotion.getName());
            }
            String quantityName = QuantityEnum.findByStock(regularQuantity);
            showln(String.format(promotionFormat, name, price) + quantityName);
        }
    }

    public void showException(final RuntimeException e) {
        System.out.println(e.getMessage());
    }

    private String format(String format, Object... args) {
        return String.format(format, args);
    }

    public void requestBenefit(final String name) {
        showln(format(REQUEST_BENEFIT, name));
    }
}
