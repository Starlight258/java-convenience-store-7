package store.domain.membership;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;

public class Membership {

    private static final int MAX_MEMBERSHIP_PRICE = 8000;
    private static final int PERCENT_TOTAL = 100;
    private static final int MEMBERSHIP_RATE = 30;

    private final Map<Product, Quantity> noPromotionProducts;

    public Membership(final Map<Product, Quantity> noPromotionProducts) {
        this.noPromotionProducts = noPromotionProducts;
    }

    public void addNoPromotionProduct(final Product product, final Quantity quantity) {
        noPromotionProducts.put(product, noPromotionProducts.getOrDefault(product, Quantity.zero()).add(quantity));
    }

    public Price calculateDiscount() {
        Price membershipPrice = getTotalNoPromotionPrice();
        membershipPrice = membershipPrice.divide(BigDecimal.valueOf(PERCENT_TOTAL)).multiply(BigDecimal.valueOf(
                MEMBERSHIP_RATE));
        if (membershipPrice.isEqualOrMoreThan(BigDecimal.valueOf(MAX_MEMBERSHIP_PRICE))) {
            return new Price(BigDecimal.valueOf(MAX_MEMBERSHIP_PRICE));
        }
        return membershipPrice;
    }

    private Price getTotalNoPromotionPrice() {
        Price totalPrice = Price.zero();
        for (Entry<Product, Quantity> entry : noPromotionProducts.entrySet()) {
            totalPrice = totalPrice.add(
                    entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue().getQuantity())));
        }
        return totalPrice;
    }
}
