package store.domain.product.stock;

public enum StockStatus {

    NOT_EXIST(-1), EMPTY(0);

    private final int value;

    StockStatus(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
