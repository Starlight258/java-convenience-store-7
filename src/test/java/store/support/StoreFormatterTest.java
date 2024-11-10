package store.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("포매터 테스트")
class StoreFormatterTest {

    @Test
    @DisplayName("인벤토리 정보를 나타내는 메세지를 만든다.")
    void 성공_인벤토리메세지() {
        // Given
        StoreFormatter formatter = new StoreFormatter(12);

        // When
        String message = formatter.makeInventoryMessage(3, "탄산2+1", "콜라", BigDecimal.valueOf(1000));

        // Then
        assertThat(message).isEqualTo("- 콜라 1,000원 3개 탄산2+1");
    }
}
