package store.domain.price;

import static org.assertj.core.api.Assertions.assertThatCode;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("가격 테스트")
class PriceTest {
    @Test
    @DisplayName("가격을 생성한다")
    void createPrice() {
        assertThatCode(() -> new Price(BigDecimal.valueOf(3000))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("가격이 실수이면 실패한다")
    void throwExceptionWhenDecimal() {
        assertCustomIllegalArgumentException(() -> new Price(new BigDecimal("3000.5")))
                .hasMessageContaining("가격은 숫자로만 이루어져야 합니다.");
    }

    @Test
    @DisplayName("가격이 음수이면 실패한다")
    void throwExceptionWhenNegative() {
        assertCustomIllegalArgumentException(() -> new Price(BigDecimal.valueOf(-3000)))
                .hasMessageContaining("가격은 음수일 수 없습니다.");
    }
}
