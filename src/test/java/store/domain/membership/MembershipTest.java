package store.domain.membership;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;

@DisplayName("멤버십 테스트")
class MembershipTest {

    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1000);
    private static final BigDecimal MAX_DISCOUNT = BigDecimal.valueOf(8000);

    private Product product;
    private Map<Product, Quantity> products;

    @BeforeEach
    void setUp() {
        product = new Product("coke", PRODUCT_PRICE);
        products = new HashMap<>();
    }

    @Test
    @DisplayName("멤버십 할인은 최대 8000원까지 가능하다.")
    void 성공_멤버십할인_멤버십가격계산() {
        products.put(product, new Quantity(265));
        Membership membership = new Membership(products);

        assertThat(membership.calculateDiscount())
                .isEqualTo(new Price(MAX_DISCOUNT));
    }
}
