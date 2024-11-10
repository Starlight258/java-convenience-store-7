package store.domain.receipt;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.inventory.Product;
import store.domain.price.Price;

public class Receipt {

    private final Map<Product, Integer> purchasedProducts;
    private final Map<Product, Integer> bonusProducts;

    public Receipt(final Map<Product, Integer> purchasedProducts, final Map<Product, Integer> bonusProducts) {
        this.purchasedProducts = purchasedProducts;
        this.bonusProducts = bonusProducts;
    }

    public void purchaseProducts(Product product, int quantity) {
        purchasedProducts.put(product, purchasedProducts.getOrDefault(product, 0) + quantity);
    }

    public void addBonusProducts(final Product product, final int bonusQuantity) {
        bonusProducts.put(product, bonusQuantity);
    }

    public Price getPromotionDiscountPrice() {
        Price promotionPrice = Price.zero();
        for (Entry<Product, Integer> entry : bonusProducts.entrySet()) {
            promotionPrice = promotionPrice.add(
                    entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return promotionPrice;
    }

    public Price getPriceToPay(Price totalPrice, Price membershipDiscountPrice) {
        totalPrice = totalPrice.subtract(getPromotionDiscountPrice());
        totalPrice = totalPrice.subtract(membershipDiscountPrice);
        return totalPrice;
    }

    public Map.Entry<Integer, Price> getTotalPurchase() {
        int totalCount = 0;
        Price totalPrice = Price.zero();
        for (Entry<Product, Integer> entry : purchasedProducts.entrySet()) {
            totalCount += entry.getValue();
            totalPrice = totalPrice.add(entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return Map.entry(totalCount, totalPrice);
    }

    public Map<Product, Integer> getPurchasedProducts() {
        return Collections.unmodifiableMap(purchasedProducts);
    }

    public Map<Product, Integer> getBonusProducts() {
        return Collections.unmodifiableMap(bonusProducts);
    }
}
