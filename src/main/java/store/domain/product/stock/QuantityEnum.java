package store.domain.product.stock;

public enum QuantityEnum {

    QUANTITY("%s개"),
    OUT_OF_STOCK("재고 없음");

    private final String name;

    QuantityEnum(final String name) {
        this.name = name;
    }

    public static String findByStock(int number) {
        if (number == 0) {
            return OUT_OF_STOCK.name;
        }
        return String.format(QUANTITY.name, number);
    }

    public String getName() {
        return name;
    }
}
