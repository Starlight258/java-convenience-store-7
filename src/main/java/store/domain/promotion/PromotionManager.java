package store.domain.promotion;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;

public class PromotionManager {

    private final Promotions promotions;

    public PromotionManager(final Promotions promotions) {
        this.promotions = promotions;
    }

    public boolean isValidPromotion(String promotionName) {
        LocalDate now = DateTimes.now().toLocalDate();
        return promotions.find(promotionName, now).isPresent();
    }

    public Promotion findByName(String promotionName) {
        return promotions.findByName(promotionName);
    }
}
