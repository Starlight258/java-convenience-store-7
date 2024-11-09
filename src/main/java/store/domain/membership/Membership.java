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

    public BigDecimal checkMembership(final Map<String, BigDecimal> totalNoPromotionPrice) {
        BigDecimal membershipPrice = BigDecimal.ZERO;
        for (Entry<String, BigDecimal> entry : totalNoPromotionPrice.entrySet()) {
            membershipPrice = membershipPrice.add(entry.getValue());
        }
        membershipPrice = membershipPrice.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(30));
        if (membershipPrice.compareTo(BigDecimal.valueOf(8000)) > 0) {
            return BigDecimal.valueOf(8000);
        }
        return membershipPrice;
    }
}
