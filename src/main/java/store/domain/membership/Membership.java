package store.domain.membership;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.inventory.Product;

public class Membership {

    private final Map<Product, Integer> noPromotionProducts;

    public Membership(final Map<Product, Integer> noPromotionProducts) {
        this.noPromotionProducts = noPromotionProducts;
    }

    public void addNoPromotionProduct(final Product product, final Integer quantity) {
        noPromotionProducts.put(product, noPromotionProducts.getOrDefault(product, 0) + quantity);
    }

    public BigDecimal calculateDiscount() {
        BigDecimal membershipPrice = getTotalNoPromotionPrice();
        membershipPrice = membershipPrice.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(30));
        if (membershipPrice.compareTo(BigDecimal.valueOf(8000)) > 0) {
            return BigDecimal.valueOf(8000);
        }
        return membershipPrice;
    }

    private BigDecimal getTotalNoPromotionPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Entry<Product, Integer> entry : noPromotionProducts.entrySet()) {
            totalPrice = totalPrice.add(entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return totalPrice;
    }
}
