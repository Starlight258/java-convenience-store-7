package store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("상품 테스트")
public class ProductTest {

    @Test
    @DisplayName("상품은 상품명과 가격을 가진다.")
    void 성공_상품() {
        // Given

        // When &
        Product coke = new Product("콜라", BigDecimal.valueOf(1000));

        // Then
        assertThat(coke).extracting("name", "price").contains("콜라", BigDecimal.valueOf(1000));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    @DisplayName("상품명은 비어있거나 null일 수 없다.")
    void 실패_상품_상품명비어있거나null(String input) {
        // Given

        // When & Then
        assertThatThrownBy(() -> new Product(input, BigDecimal.valueOf(1000)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("상품명은 비어있거나 null일 수 없습니다.");
    }

    @Test
    @DisplayName("가격은 null일 수 없다.")
    void 실패_상품_가격null() {
        // Given

        // When & Then
        assertThatThrownBy(() -> new Product("콜라", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("[ERROR] 가격은 null일 수 없습니다.");
    }
}
