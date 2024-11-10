package store.domain.promotion;

import java.time.LocalDate;
import store.domain.quantity.Quantity;
import store.exception.ExceptionMessage;

public class Promotion {

    public static final ExceptionMessage INVALID_DATE = new ExceptionMessage("시작 날짜는 이후 날짜 이전일 수 없습니다.");
    public static final ExceptionMessage INVALID_NULL_EMPTY = new ExceptionMessage("프로모션 이름이 비어있거나 null일 수 없습니다.");

    private final String promotionName;
    private final Quantity purchaseQuantity;
    private final Quantity bonusQuantity;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(final String promotionName, final Quantity purchaseQuantity, final Quantity bonusQuantity,
                     final LocalDate startDate, final LocalDate endDate) {
        validate(promotionName, startDate, endDate);
        this.promotionName = promotionName;
        this.purchaseQuantity = purchaseQuantity;
        this.bonusQuantity = bonusQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isPromotionPeriod(LocalDate now) {
        if (now.isEqual(startDate) || now.isEqual(endDate)) {
            return true;
        }
        return now.isAfter(startDate) && now.isBefore(endDate);
    }

    public boolean isValid(final String promotionName, final LocalDate now) {
        return this.promotionName.equals(promotionName) &&
                (now.isEqual(startDate) || now.isAfter(startDate)) &&
                (now.isEqual(endDate) || now.isBefore(endDate));
    }

    private void validate(final String promotionName, final LocalDate startDate, final LocalDate endDate) {
        validatePromotionName(promotionName);
        validateDate(startDate, endDate);
    }

    private void validateDate(final LocalDate startDate, final LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(INVALID_DATE.getMessage());
        }
    }

    private void validatePromotionName(final String promotionName) {
        if (promotionName == null || promotionName.isBlank()) {
            throw new IllegalArgumentException(INVALID_NULL_EMPTY.getMessage());
        }
    }

    public Quantity getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public Quantity getBonusQuantity() {
        return bonusQuantity;
    }
}
