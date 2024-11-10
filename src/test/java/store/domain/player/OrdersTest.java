package store.domain.player;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.quantity.Quantity;

@DisplayName("초기 구매 폼 테스트")
class OrdersTest {

    @Test
    @DisplayName("상품을 추가한다.")
    void 성공_상품추가() {
        // Given
        Orders forms = new Orders(new HashMap<>() {{
            put("coke", new Quantity(3));
        }});

        // When
        forms.put("juice", new Quantity(4));

        // Then
        assertThat(forms).extracting("productsToBuy").isEqualTo(new HashMap<>() {{
            put("coke", new Quantity(3));
            put("juice", new Quantity(4));
        }});
    }
}
