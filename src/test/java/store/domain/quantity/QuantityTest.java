package store.domain.quantity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("수량 테스트")
class QuantityTest {

    @Test
    @DisplayName("수량을 생성한다.")
    void 성공_생성() {
        // Given

        // When & Then
        assertThatCode(() -> {
            new Quantity(3);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구매 수량이 음수이면 예외가 발생한다")
    void 실패_생성_구매수량() {
        // Given

        // When & Then
        assertThatThrownBy(() -> new Quantity(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("수량은 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("0개의 수량을 나타내는 수량을 생성한다.")
    void 성공_생성_0() {
        // Given

        // When & Then
        assertThatCode(() -> {
            Quantity.zero();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("수량끼리 더한다.")
    void 성공_더하기() {
        // Given
        Quantity quantity = Quantity.zero();

        // When
        Quantity addedQuantity = quantity.add(new Quantity(3));

        // Then
        assertThat(addedQuantity).isEqualTo(new Quantity(3));
    }

    @Test
    @DisplayName("수량끼리 뻰다.")
    void 성공_빼기() {
        // Given
        Quantity quantity = new Quantity(10);

        // When
        Quantity subtractedQuantity = quantity.subtract(new Quantity(3));

        // Then
        assertThat(subtractedQuantity).isEqualTo(new Quantity(7));
    }

    @Test
    @DisplayName("뺀 수량이 음수이면 예외가 발생한다.")
    void 실패_빼기() {
        // Given
        Quantity quantity = Quantity.zero();

        // When
        assertThatThrownBy(() -> quantity.subtract(new Quantity(3)))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("수량은 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("수량끼리 곱한다.")
    void 성공_곱하기() {
        // Given
        Quantity quantity = new Quantity(10);

        // When
        Quantity multipliedQuantity = quantity.multiply(new Quantity(3));

        // Then
        assertThat(multipliedQuantity).isEqualTo(new Quantity(30));
    }

    @Test
    @DisplayName("수량끼리 나눈다.")
    void 성공_나누기() {
        // Given
        Quantity quantity = new Quantity(10);

        // When
        Quantity dividedQuantity = quantity.divide(new Quantity(3));

        // Then
        assertThat(dividedQuantity).isEqualTo(new Quantity(3));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    @DisplayName("수량이 비교할 수량보다 크거나 같은지 확인한다.")
    void 성공_크거나같음(int input) {
        // Given
        Quantity quantity = new Quantity(3);

        // When & Then
        assertThat(quantity.isMoreThanEqual(new Quantity(input))).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"2,true", "3,false"})
    @DisplayName("수량이 비교할 수량보다 큰지 확인한다.")
    void 성공_큼(int input, boolean expected) {
        // Given
        Quantity quantity = new Quantity(3);

        // When & Then
        assertThat(quantity.isMoreThan(new Quantity(input))).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"4,true", "3,false"})
    @DisplayName("수량이 비교할 수량보다 작은지 확인한다.")
    void 성공_작음(int input, boolean expected) {
        // Given
        Quantity quantity = new Quantity(3);

        // When & Then
        assertThat(quantity.isLessThan(new Quantity(input))).isEqualTo(expected);
    }

    @Test
    @DisplayName("수량이 0인지 확인한다.")
    void 성공_값이0인지확인() {
        // Given
        Quantity quantity = new Quantity(0);

        // When & Then
        assertThat(quantity.hasZeroValue()).isTrue();
    }
}
