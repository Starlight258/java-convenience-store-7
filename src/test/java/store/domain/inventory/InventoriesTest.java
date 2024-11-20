package store.domain.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.Store;
import store.domain.membership.Membership;
import store.domain.order.Order;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;

@DisplayName("재고 집합 테스트")
public class InventoriesTest {

    private Product product;
    private Inventories inventories;
    private Inventory withPromotion;
    private Store store;

    @BeforeEach
    void setUp() {
        product = new Product("coke", BigDecimal.valueOf(1000));
        withPromotion = new Inventory(product, 10, "탄산2+1");
        Inventory withoutPromotion = new Inventory(product, 10, "null");
        inventories = new Inventories(List.of(withPromotion, withoutPromotion));
        store = new Store(new Receipt(new HashMap<>(), new HashMap<>()), new Membership(new HashMap<>()));
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
    @DisplayName("상품이 없을 경우 예외가 발생한다")
    void 실패_안내_상품없음() {
        assertCustomIllegalArgumentException(() -> inventories.checkStock(new Order("[invalid-3]")))
                .hasMessageContaining("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("재고 수량 이상으로 구매할 경우 예외가 발생한다")
    void 실패_안내_재고없음() {
        Map<String, Quantity> items = Map.of("coke", new Quantity(33));
        assertCustomIllegalArgumentException(() -> inventories.checkStock(new Order("[coke-33]")))
                .hasMessageContaining("재고 수량을 초과하여 구매할 수 없습니다.");
    }

    @Test
    @DisplayName("프로모션 없이 구매한다.")
    void 성공_프로모션없이구매() {
        // given
        Quantity purchaseQuantity = new Quantity(3);
        // when
        inventories.buyProductWithoutPromotion(purchaseQuantity, store);
        // then
        assertThat(withPromotion.getQuantity().getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("구매 수량을 여러 재고에서 가져온다.")
    void 성공_여러재고() {
        // given
        Quantity purchaseQuantity = new Quantity(11);
        // when
        inventories.buyProductWithoutPromotion(purchaseQuantity, store);
        // then
        assertThat(withPromotion.getQuantity().getQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("프로모션이 없는 상품을 찾을 수 있다")
    void 성공_프로모션없는상품조회() {
        // when
        Inventory inventory = inventories.findNoPromotionInventory();

        // then
        assertThat(inventory.hasNoPromotion()).isTrue();
        assertThat(inventory.getProductName()).isEqualTo("coke");
    }

    @Test
    @DisplayName("프로모션 가능한 상품들을 반환한다.")
    void 성공_프로모션상품반환() {
        Inventories inventories = new Inventories(List.of(
                new Inventory(new Product("coke", BigDecimal.valueOf(1000)), 10, "null")
        ));
        assertThatCode(() -> inventories.findProducts("coke"))
                .doesNotThrowAnyException();
    }
}
