package store.domain.promotion;

import java.time.LocalDate;
import java.util.Objects;

public class Promotion {

    private final String name;
    private final int buyQuantity;
    private final int getQuantity;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(final String name, final int buyQuantity, final int getQuantity, final LocalDate startDate,
                     final LocalDate endDate) {
        this.name = name;
        this.buyQuantity = buyQuantity;
        this.getQuantity = getQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean hasName(final String name) {
        return this.name.equals(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Promotion promotion)) {
            return false;
        }
        return Objects.equals(name, promotion.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }
}
