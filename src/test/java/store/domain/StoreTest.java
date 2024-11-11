package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;

@DisplayName("가게 테스트")
class StoreTest {

    private final Product product = new Product("콜라", BigDecimal.valueOf(1000));
    private final Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
    private final Store store = new Store(receipt, new Membership(new HashMap<>()));

    @Test
    @DisplayName("프로모션이 적용되지 않는 상품을 기록한다")
    void recordNoPromotionProduct() {
        store.noteNoPromotionProduct(product, new Quantity(3));
        assertThat(store).extracting("receipt").extracting("purchasedProducts")
                .isEqualTo(new HashMap<>() {{
                    put(product, new Quantity(3));
                }});
    }

    @Test
    @DisplayName("구매한 상품을 기록한다")
    void recordPurchasedProduct() {
        store.notePurchaseProduct(product, new Quantity(3));
        assertThat(store).extracting("receipt").extracting("purchasedProducts")
                .isEqualTo(new HashMap<>() {{
                    put(product, new Quantity(3));
                }});
    }

    @Test
    @DisplayName("보너스 상품을 기록한다")
    void recordBonusProduct() {
        store.noteBonusProduct(product, new Quantity(3));
        assertThat(store).extracting("receipt").extracting("bonusProducts")
                .isEqualTo(new HashMap<>() {{
                    put(product, new Quantity(3));
                }});
    }

    @Test
    @DisplayName("추가 증정 상품을 기록한다")
    void recordAdditionalProduct() {
        store.noteAddingMoreQuantity(product, new Quantity(2), new Quantity(1));
        assertThat(store).extracting("receipt").extracting("purchasedProducts").isEqualTo(new HashMap<>() {{
            put(product, new Quantity(1));
        }});
    }
}
