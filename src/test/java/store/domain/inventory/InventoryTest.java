package store.domain.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("재고 테스트")
public class InventoryTest {

    @Test
    @DisplayName("상품의 재고에는 상품명, 가격, 수량, 프로모션 이름을 가진다")
    void 성공_생성() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        String promotionName = "탄산2+1";

        // When
        Inventory inventory = new Inventory(product, 10, promotionName);

        // Then
        assertAll(
                () -> assertThat(inventory).extracting("product")
                        .isEqualTo(new Product("coke", BigDecimal.valueOf(1000))),
                () -> assertThat(inventory).extracting("quantity", "promotionName").contains(10, promotionName)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("프로모션은 비어있거나 null일 수 있다.")
    void 성공_생성_프로모션비어있거나null(String input) {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));

        // When
        Inventory inventory = new Inventory(product, 10, input);

        // Then
        assertThat(inventory).extracting("product").isEqualTo(new Product("coke", BigDecimal.valueOf(1000)));
        assertThat(inventory).extracting("quantity").isEqualTo(10);
        assertThat(inventory).extracting("promotionName").isEqualTo(input);
    }

    @Test
    @DisplayName("개수는 음수일 수 없습니다.")
    void 실패_생성_개수음수() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        String promotionName = "탄산2+1";

        // When & Then
        assertThatThrownBy(() -> new Inventory(product, -1, promotionName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("[ERROR] 개수는 음수일 수 없습니다.");
    }

    @Nested
    @DisplayName("구매")
    class buyTest {

        @Test
        @DisplayName("성공적으로 구매한다.")
        void 성공_구매() {
            // Given
            Product product = new Product("coke", BigDecimal.valueOf(1000));
            String promotionName = "탄산2+1";
            Inventory inventory = new Inventory(product, 10, promotionName);

            // When
            inventory.subtract(3);

            // When & Then
            assertThat(inventory.getQuantity()).isEqualTo(7);
        }

        @Test
        @DisplayName("재고가 부족한 경우 예외가 발생한다.")
        void 실패_구매_재고부족() {
            // Given
            Product product = new Product("coke", BigDecimal.valueOf(1000));
            String promotionName = "탄산2+1";
            Inventory inventory = new Inventory(product, 10, promotionName);

            // When & Then
            assertThatThrownBy(() -> inventory.subtract(14))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageStartingWith("[ERROR]")
                    .hasMessageContaining("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }

        @Test
        @DisplayName("프로모션 가능한 상품들을 반환한다.")
        void 성공_프로모션상품반환() {
            // Given
            Product coke = new Product("coke", BigDecimal.valueOf(1000));
            Product milk = new Product("milk", BigDecimal.valueOf(1000));
            String promotionName = "탄산2+1";
            Inventory inventoryWithPromotion = new Inventory(coke, 10, promotionName);
            Inventory inventoryWithNoPromotion = new Inventory(milk, 10, "null");
            Inventories inventories = new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
            // When & Then
            assertThatCode(() -> {
                inventories.findProducts("coke");
            }).doesNotThrowAnyException();
        }
    }
}
