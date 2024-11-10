package store.domain.receipt;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;

public class Receipt {

    private final Map<Product, Quantity> purchasedProducts;
    private final Map<Product, Quantity> bonusProducts;

    public Receipt(final Map<Product, Quantity> purchasedProducts, final Map<Product, Quantity> bonusProducts) {
        this.purchasedProducts = purchasedProducts;
        this.bonusProducts = bonusProducts;
    }

    public void purchaseProducts(Product product, Quantity quantity) {
        purchasedProducts.put(product, purchasedProducts.getOrDefault(product, Quantity.zero()).add(quantity));
    }

    public void addBonusProducts(final Product product) {
        bonusProducts.put(product, bonusProducts.getOrDefault(product, Quantity.zero()).add(Quantity.one()));
    }

    public Price getPromotionDiscountPrice() {
        Price promotionPrice = Price.zero();
        for (Entry<Product, Quantity> entry : bonusProducts.entrySet()) {
            promotionPrice = promotionPrice.add(
                    entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue().getQuantity())));
        }
        return promotionPrice;
    }

    public Price getPriceToPay(Price totalPrice, Price membershipDiscountPrice) {
        totalPrice = totalPrice.subtract(getPromotionDiscountPrice());
        totalPrice = totalPrice.subtract(membershipDiscountPrice);
        return totalPrice;
    }

    public Map.Entry<Quantity, Price> getTotalPurchase() {
        Quantity totalCount = Quantity.zero();
        Price totalPrice = Price.zero();
        for (Entry<Product, Quantity> entry : purchasedProducts.entrySet()) {
            totalCount = totalCount.add(entry.getValue());
            totalPrice = totalPrice.add(
                    entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue().getQuantity())));
        }
        return Map.entry(totalCount, totalPrice);
    }

    public Map<Product, Quantity> getPurchasedProducts() {
        return Collections.unmodifiableMap(purchasedProducts);
    }

    public Map<Product, Quantity> getBonusProducts() {
        return Collections.unmodifiableMap(bonusProducts);
    }
}
