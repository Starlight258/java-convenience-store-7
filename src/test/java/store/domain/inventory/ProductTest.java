package store.domain.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.price.Price;

@DisplayName("상품 테스트")
public class ProductTest {

    private static final String PRODUCT_NAME = "콜라";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1000);

    @Test
    @DisplayName("상품은 상품명과 가격을 가진다.")
    void createProduct() {
        Product product = new Product(PRODUCT_NAME, PRODUCT_PRICE);

        assertThat(product)
                .extracting("name", "price")
                .contains(PRODUCT_NAME, new Price(PRODUCT_PRICE));
    }

    @Test
    @DisplayName("가격은 null일 수 없다.")
    void 실패_상품_가격null() {
        assertCustomIllegalArgumentException(() -> new Product(PRODUCT_NAME, null))
                .hasMessageContaining("인자 값은 null일 수 없습니다.");
    }
}
