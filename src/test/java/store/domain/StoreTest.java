package store.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @Test
    @DisplayName("프로모션이 적용되지 않는 상품을 기록한다.")
    void 성공_프로모션아닌상품() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);
        Product product = new Product("콜라", BigDecimal.valueOf(1000));

        // When
        store.noteNoPromotionProduct(product, new Quantity(3));

        // Then
        assertAll(
                () -> assertThat(store).extracting("receipt").extracting("purchasedProducts")
                        .isEqualTo(new HashMap<>() {{
                            put(product, new Quantity(3));
                        }}),
                () -> assertThat(store).extracting("membership").extracting("noPromotionProducts")
                        .isEqualTo(new HashMap<>() {{
                            put(product, new Quantity(3));
                        }})
        );
    }

    @Test
    @DisplayName("구매한 상품을 기록한다.")
    void 성공_구매상품기록() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);
        Product product = new Product("콜라", BigDecimal.valueOf(1000));

        // When
        store.notePurchaseProduct(product, new Quantity(3));

        // Then
        assertThat(store).extracting("receipt").extracting("purchasedProducts").isEqualTo(new HashMap<>() {{
            put(product, new Quantity(3));
        }});
    }

    @Test
    @DisplayName("보너스 상품을 기록한다.")
    void 성공_보너스상품() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);
        Product product = new Product("콜라", BigDecimal.valueOf(1000));

        // When
        store.noteBonusProduct(product);

        // Then
        assertThat(store).extracting("receipt").extracting("bonusProducts").isEqualTo(new HashMap<>() {{
            put(product, new Quantity(1));
        }});
    }

    @Test
    @DisplayName("추가 증정 상품을 기록한다.")
    void 성공_추가증정상품() {
        // Given
        Receipt receipt = new Receipt(new HashMap<>(), new HashMap<>());
        Membership membership = new Membership(new HashMap<>());
        Store store = new Store(receipt, membership);
        Product product = new Product("콜라", BigDecimal.valueOf(1000));

        // When
        store.noteAddingMoreQuantity(product);

        // Then
        assertThat(store).extracting("receipt").extracting("bonusProducts").isEqualTo(new HashMap<>() {{
            put(product, new Quantity(1));
        }});
        assertThat(store).extracting("receipt").extracting("purchasedProducts").isEqualTo(new HashMap<>() {{
            put(product, new Quantity(1));
        }});
    }
}
