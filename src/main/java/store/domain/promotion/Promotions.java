package store.domain.promotion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import store.exception.CustomIllegalArgumentException;
import store.util.InputValidator;

public class Promotions {

    private static final String PROMOTION_NOT_FOUND = "Promotion 이름을 찾을 수 없습니다.";

    private final List<Promotion> promotions;

    public Promotions(final List<Promotion> promotions) {
        validatePromotions(promotions);
        this.promotions = promotions;
    }

    public Optional<Promotion> find(final String promotionName, final LocalDate now) {
        return promotions.stream()
                .filter(promotion -> promotion.isValid(promotionName, now))
                .findFirst();
    }

    public Promotion findByName(final String promotionName) {
        return promotions.stream()
                .filter(promotion -> promotion.hasName(promotionName))
                .findFirst()
                .orElseThrow(() -> new CustomIllegalArgumentException(PROMOTION_NOT_FOUND));
    }

    private void validatePromotions(final List<Promotion> promotions) {
        InputValidator.validateNotNull(promotions);
    }
}
