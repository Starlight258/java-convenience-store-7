package store.domain.membership;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.inventory.Product;
import store.domain.price.Price;

class MembershipTest {

    @Test
    @DisplayName("멤버십 할인은 최대 8000원까지 가능하다.")
    void 성공_멤버십할인_멤버십가격계산() {
        // Given
        Product coke = new Product("coke", BigDecimal.valueOf(1000));
        Map<Product, Integer> noPromotionProducts = new HashMap<>() {{
            put(coke, 265);
        }};
        Membership membership = new Membership(noPromotionProducts);

        // When
        Price totalMembershipPrice = membership.calculateDiscount();

        // Then
        assertThat(totalMembershipPrice).isEqualTo(new Price(BigDecimal.valueOf(8000)));
    }
}
