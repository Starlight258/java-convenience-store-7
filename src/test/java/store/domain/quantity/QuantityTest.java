package store.domain.quantity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static store.util.CustomExceptionAssertions.assertCustomIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("수량 테스트")
class QuantityTest {

    @Test
    @DisplayName("수량을 생성한다")
    void createQuantity() {
        assertThatCode(() -> new Quantity(3)).doesNotThrowAnyException();
        assertThatCode(Quantity::zero).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구매 수량이 음수이면 예외가 발생한다")
    void throwExceptionWhenNegative() {
        assertCustomIllegalArgumentException(() -> new Quantity(-1))
                .hasMessageContaining("수량은 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("수량 사칙연산을 수행한다")
    void calculateQuantities() {
        Quantity quantity = new Quantity(10);
        assertThat(quantity.add(new Quantity(3))).isEqualTo(new Quantity(13));
        assertThat(quantity.subtract(new Quantity(3))).isEqualTo(new Quantity(7));
        assertThat(quantity.multiply(new Quantity(3))).isEqualTo(new Quantity(30));
        assertThat(quantity.divide(new Quantity(2))).isEqualTo(new Quantity(5));
    }

    @ParameterizedTest
    @CsvSource({"2,true,false", "3,false,false", "4,false,true"})
    @DisplayName("수량을 비교한다")
    void compareQuantities(int input, boolean isMore, boolean isLess) {
        Quantity quantity = new Quantity(3);
        assertThat(quantity.isMoreThan(new Quantity(input))).isEqualTo(isMore);
        assertThat(quantity.isLessThan(new Quantity(input))).isEqualTo(isLess);
    }
}
