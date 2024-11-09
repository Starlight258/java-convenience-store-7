package store.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import store.util.Parser;

@DisplayName("파서 테스트")
public class ParserTest {

    @Test
    @DisplayName("파싱에 성공한다.")
    void 성공_파싱() {
        // Given
        String input = "2024-12-31";

        // When
        LocalDate date = Parser.parseToLocalDate(input);

        // Then
        assertThat(date).isEqualTo(LocalDate.of(2024, 12, 31));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-12-41", "20241231", "abc"})
    @DisplayName("유효한 날짜 형식이 아닐 경우 예외가 발생한다.")
    void 실패_파싱(String input) {
        // Given

        // When
        assertThatThrownBy(() -> Parser.parseToLocalDate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR]")
                .hasMessageContaining("유효한 날짜형식이 아닙니다.");
    }
}
