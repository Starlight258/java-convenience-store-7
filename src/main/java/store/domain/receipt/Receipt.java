package store.domain.receipt;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.inventory.Product;

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

    public BigDecimal getPromotionDiscountPrice() {
        BigDecimal promotionPrice = BigDecimal.ZERO;
        for (Entry<Product, Integer> entry : bonusProducts.entrySet()) {
            promotionPrice = promotionPrice.add(
                    entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return promotionPrice;
    }

    public BigDecimal getPriceToPay(BigDecimal totalPrice, BigDecimal membershiptDiscountPrice) {
        totalPrice = totalPrice.subtract(getPromotionDiscountPrice());
        totalPrice = totalPrice.subtract(membershiptDiscountPrice);
        return totalPrice;
    }

    public Map.Entry<Integer, BigDecimal> getTotalPurchase() {
        int totalCount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
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
