package store.domain.promotion;

import java.util.List;
import java.util.Optional;

public class Promotions {

    private final List<Promotion> promotions;

    public Promotions(final List<Promotion> promotions) {
        validatePromotions(promotions);
        this.promotions = promotions;
    }

    private void validatePromotions(final List<Promotion> promotions) {
        if (promotions == null) {
            throw new IllegalArgumentException("[ERROR] 프로모션 리스트는 null일 수 없습니다.");
        }
    }

    public Optional<Promotion> find(final String name) {
        for (Promotion promotion : promotions) {
            if (promotion.isSameName(name)) {
                return Optional.of(promotion);
            }
        }
        return Optional.empty();
    }
}
