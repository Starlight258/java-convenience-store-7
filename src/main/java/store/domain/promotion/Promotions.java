package store.domain.promotion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import store.util.InputValidator;

public class Promotions {

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

    private void validatePromotions(final List<Promotion> promotions) {
        InputValidator.validateNotNull(promotions);
    }
}
