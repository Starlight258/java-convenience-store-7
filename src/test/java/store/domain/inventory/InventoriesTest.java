package store.domain.inventory;

import static org.assertj.core.api.Assertions.assertThatCode;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.quantity.Quantity;

@DisplayName("재고 집합 테스트")
public class InventoriesTest {

    private Product product;
    private Inventories inventories;

    @BeforeEach
    void setUp() {
        product = new Product("coke", BigDecimal.valueOf(1000));
        Inventory withPromotion = new Inventory(product, 10, "탄산2+1");
        Inventory withoutPromotion = new Inventory(product, 10, "null");
        inventories = new Inventories(List.of(withPromotion, withoutPromotion));
    }

    @Test
    @DisplayName("인벤토리 집합을 생성한다.")
    void 성공_생성() {
        assertThatCode(() -> new Inventories(List.of(
                new Inventory(product, 10, "탄산2+1"))))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("인벤토리 집합이 null이면 예외가 발생한다.")
    void 성공_실패_null() {
        assertCustomIllegalArgumentException(() -> new Inventories(null))
                .hasMessageContaining("인자 값은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("상품은 null일 수 없다.")
    void 실패_생성_상품null() {
        assertCustomIllegalArgumentException(() -> new Inventory(null, 10, "탄산2+1"))
                .hasMessageContaining("인자 값은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("수량은 음수일 수 없습니다.")
    void 실패_생성_수량음수() {
        assertCustomIllegalArgumentException(() -> new Inventory(product, -1, "탄산2+1"))
                .hasMessageContaining("수량은 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("상품이 없을 경우 예외가 발생한다")
    void 실패_안내_상품없음() {
        Map<String, Quantity> items = Map.of("invalid", new Quantity(3));
        assertCustomIllegalArgumentException(() -> inventories.getPurchasedItems(new HashMap<>(items)))
                .hasMessageContaining("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("재고 수량 이상으로 구매할 경우 예외가 발생한다")
    void 실패_안내_재고없음() {
        Map<String, Quantity> items = Map.of("coke", new Quantity(33));
        assertCustomIllegalArgumentException(() -> inventories.getPurchasedItems(new HashMap(items)))
                .hasMessageContaining("재고 수량을 초과하여 구매할 수 없습니다.");
    }
}
