package store.domain.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import store.domain.quantity.Quantity;

@DisplayName("재고 테스트")
public class InventoryTest {

    private Product product;
    private String promotionName;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        product = new Product("coke", BigDecimal.valueOf(1000));
        promotionName = "탄산2+1";
        inventory = new Inventory(product, 10, promotionName);
    }

    @Test
    @DisplayName("상품의 재고에는 상품명, 가격, 수량, 프로모션 이름을 가진다")
    void 성공_생성() {
        assertThat(inventory)
                .extracting("product", "quantity", "promotionName")
                .containsExactly(product, new Quantity(10), promotionName);
    }

    @Test
    @DisplayName("프로모션 이름은 문자열 null이 가능하다.")
    void 성공_생성_null() {
        assertThatCode(() -> {
            new Inventory(product, 10, "null");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상품은 null일 수 없다.")
    void 실패_생성_상품null() {
        assertCustomIllegalArgumentException(() -> new Inventory(null, 10, "탄산2+1"))
                .hasMessageContaining("인자 값은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("수량은 음수일 수 없다.")
    void 실패_생성_수량음수() {
        assertCustomIllegalArgumentException(() -> new Inventory(product, -1, "탄산2+1"))
                .hasMessageContaining("수량은 음수일 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("프로모션명은 비어있거나 공백일 수 없다.")
    void createInventoryWithEmptyPromotion(String input) {
        assertCustomIllegalArgumentException(() -> new Inventory(product, 10, input))
                .hasMessageContaining("인자 값은 null이거나 공백일 수 없습니다");
    }

    @Test
    @DisplayName("개수는 음수일 수 없습니다.")
    void 실패_생성_개수음수() {
        assertCustomIllegalArgumentException(() -> new Inventory(product, -1, promotionName))
                .hasMessageContaining("수량은 음수일 수 없습니다.");
    }

    @Nested
    @DisplayName("구매 테스트")
    class Purchase {
        @Test
        @DisplayName("성공적으로 구매한다.")
        void 성공_구매() {
            Quantity subtractedQuantity = inventory.subtract(new Quantity(3));
            assertThat(subtractedQuantity).isEqualTo(new Quantity(7));
        }

        @Test
        @DisplayName("재고가 부족한 경우 예외가 발생한다.")
        void 실패_구매_재고부족() {
            assertCustomIllegalArgumentException(() -> inventory.subtract(new Quantity(14)))
                    .hasMessageContaining("재고 수량을 초과하여 구매할 수 없습니다.");
        }
    }
}
