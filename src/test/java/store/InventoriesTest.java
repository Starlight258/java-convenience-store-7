package store;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("재고 집합 테스트")
public class InventoriesTest {

    @Test
    @DisplayName("인벤토리 집합을 생성한다.")
    void 성공_생성() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));
        String promotionName = "탄산2+1";
        Inventory inventoryWithPromotion = new Inventory(product, 10, promotionName);
        Inventory inventoryWithNoPromotion = new Inventory(product, 10, null);

        // When & Then
        assertThatCode(() -> {
            new Inventories(List.of(inventoryWithPromotion, inventoryWithNoPromotion));
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("인벤토리 집합이 null이면 예외가 발생한다.")
    void 성공_실패_null() {
        // Given

        // When & Then
        assertThatThrownBy(() -> new Inventories(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("인벤토리 리스트는 null일 수 없습니다.");
    }

    @Test
    @DisplayName("상품은 null일 수 없다.")
    void 실패_생성_상품null() {
        // Given
        String promotionName = "탄산2+1";

        // When & Then
        assertThatThrownBy(() -> new Inventory(null, 10, promotionName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("[ERROR] 상품은 null일 수 없습니다.");
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

    @Test
    @DisplayName("프로모션이 null일 수 있다.")
    void 성공_생성_프로모션null() {
        // Given
        Product product = new Product("coke", BigDecimal.valueOf(1000));

        // When & Then
        assertThatCode(() -> {
            new Inventory(product, 10, null);
        }).doesNotThrowAnyException();
    }
}
