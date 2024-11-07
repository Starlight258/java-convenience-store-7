package store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("재고 테스트")
public class InventoryTest {

    @Test
    @DisplayName("상품의 재고에는 상품명, 가격, 수량, 프로모션 이름을 가진다")
    void 성공_생성() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Promotion cokePromotion = new Promotion("탄산2+1", 2, 1, startDate, endDate);

        // When
        Inventory inventory = new Inventory(product, 10, cokePromotion);

        // Then
        assertAll(
                () -> assertThat(inventory).extracting("product")
                        .isEqualTo(new Product("coke", BigDecimal.valueOf(1000))),
                () -> assertThat(inventory).extracting("quantity", "promotion").contains(10, cokePromotion)
        );
    }

    @Test
    @DisplayName("프로모션이 null일 수 있다.")
    void 성공_생성_프로모션null() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));

        // When
        Inventory inventory = new Inventory(product, 10, null);

        // Then
        assertThat(inventory).extracting("product").isEqualTo(new Product("coke", BigDecimal.valueOf(1000)));
        assertThat(inventory).extracting("quantity").isEqualTo(10);
        assertThat(inventory).extracting("promotion").isEqualTo(null);
    }

    @Test
    @DisplayName("개수는 음수일 수 없습니다.")
    void 실패_생성_개수음수() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Promotion cokePromotion = new Promotion("탄산2+1", 2, 1, startDate, endDate);

        // When & Then
        assertThatThrownBy(() -> new Inventory(product, -1, cokePromotion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("[ERROR] 개수는 음수일 수 없습니다.");
    }

    @Nested
    @DisplayName("수량 관리")
    class manageQuantityTest {

        @ParameterizedTest
        @CsvSource({"3,true", "11,false"})
        void 성공_수량관리(int quantity, boolean canBuy) {
            // Given
            Product product = new Product("coke", BigDecimal.valueOf(1000));
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);
            Promotion cokePromotion = new Promotion("탄산2+1", 2, 1, startDate, endDate);

            Inventory inventory = new Inventory(product, 10, cokePromotion);

            // When & Then
            assertThat(inventory.buy(quantity)).isEqualTo(canBuy);
        }
    }
}
