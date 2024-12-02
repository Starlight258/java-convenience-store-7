package store.domain.promotion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PromotionTest {

    @ParameterizedTest
    @CsvSource({"1,false", "10,true", "20,true", "30,false"})
    @DisplayName("프로모션 유효기간을 확인한다.")
    void isValidPromotion(int day, boolean expected) {
        // Given
        Promotion promotion = makePromotion();
        LocalDate now = makeLocalDate(2024, 12, day);

        // When & Then
        assertThat(promotion.isValidPromotion(now)).isEqualTo(expected);
    }

    private Promotion makePromotion() {
        LocalDate startDate = makeLocalDate(2024, 12, 10);
        LocalDate endDate = makeLocalDate(2024, 12, 20);
        return new Promotion("콜라", 2, 1, startDate, endDate);
    }

    private LocalDate makeLocalDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }


    @Test
    void getUnitQuantity() {
    }

    @Test
    void getBuyQuantity() {
    }
}
