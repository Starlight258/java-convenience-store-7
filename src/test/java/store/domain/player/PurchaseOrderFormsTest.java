package store.domain.player;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("초기 구매 폼 테스트")
class PurchaseOrderFormsTest {

    @Test
    @DisplayName("상품을 추가한다.")
    void 성공_상품추가() {
        // Given
        PurchaseOrderForms forms = new PurchaseOrderForms(new HashMap<>() {{
            put("coke", 3);
        }});

        // When
        forms.put("juice", 4);

        // Then
        assertThat(forms).extracting("productsToBuy").isEqualTo(new HashMap<>() {{
            put("coke", 3);
            put("juice", 4);
        }});
    }
}
