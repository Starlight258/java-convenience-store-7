package store.view;

import java.util.Map.Entry;
import store.domain.product.Product;
import store.domain.product.stock.Inventory;
import store.domain.product.stock.QuantityEnum;
import store.domain.product.stock.Stock;
import store.domain.promotion.Promotion;

public class OutputView {

    private static final String LINE = System.lineSeparator();
    private static final String WELCOME = "안녕하세요. W편의점입니다." + LINE + "현재 보유하고 있는 상품입니다.";

    public void showWelcome() {
        showln(WELCOME + LINE);
    }

    private void showln(String message) {
        System.out.println(message);
    }

    public void showInventory(final Inventory inventory) {
        String promotionFormat = "- %s %,d원 ";
        for (Entry<Product, Stock> entry : inventory.getInventory().entrySet()) {
            Product product = entry.getKey();
            Stock stock = entry.getValue();
            Promotion promotion = stock.getPromotion();
            String name = product.getName();
            int price = product.getPrice();
            int promotionQuantity = stock.getPromotionQuantity();
            int regularQuantity = stock.getRegularQuantity();
            // 일반 재고만 있는 경우
            if (promotionQuantity != -1) {
                String quantityName = QuantityEnum.findByStock(promotionQuantity);
                showln(String.format(promotionFormat, name, price) + quantityName + " " + promotion.getName());
            }
            // 프로모션 재고만 있는 경우
            String quantityName = QuantityEnum.findByStock(regularQuantity);
            showln(String.format(promotionFormat, name, price) + quantityName);
        }
    }
}
