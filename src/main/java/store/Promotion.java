package store;

import java.time.LocalDate;
import java.util.Objects;

public class Promotion {

    public static final String ERROR = "[ERROR] ";

    private final String promotionName;
    private final int purchaseQuantity;
    private final int bonusQuantity;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(final String promotionName, final int purchaseQuantity, final int bonusQuantity,
                     final LocalDate startDate, final LocalDate endDate) {
        validate(promotionName, purchaseQuantity, bonusQuantity, startDate, endDate);
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

    private void validate(final String promotionName, final int purchaseQuantity, final int getQuantity,
                          final LocalDate startDate, final LocalDate endDate) {
        validatePromotionName(promotionName);
        validateQuantity(purchaseQuantity);
        validateQuantity(getQuantity);
        validateDate(startDate, endDate);
    }

    private void validateDate(final LocalDate startDate, final LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(ERROR + "시작 날짜는 이후 날짜 이전일 수 없습니다.");
        }
    }

    private void validatePromotionName(final String promotionName) {
        if (promotionName == null || promotionName.isBlank()) {
            throw new IllegalArgumentException(ERROR + "프로모션 이름이 비어있거나 null일 수 없습니다.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ERROR + "구매 수량이 0 또는 음수일 수 없습니다.");
        }
    }

    public boolean isSameName(final String name) {
        return Objects.equals(this.promotionName, name);
    }

    public int getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public int getBonusQuantity() {
        return bonusQuantity;
    }
}
