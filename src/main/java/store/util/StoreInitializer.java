package store.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;

public class StoreInitializer {

    private static final String NULL = "null";

    public Inventories loadInventories() {
        List<String> inventories = readInventories();
        Inventories fromInventories = new Inventories(inventories.stream()
                .map(StoreSplitter::split)
                .map(this::createInventory)
                .toList());
        addNoPromotionInventories(fromInventories);
        return fromInventories;
    }

    public Promotions loadPromotions() {
        List<String> promotionsFromSource = readPromotions();
        return new Promotions(promotionsFromSource.stream()
                .map(StoreSplitter::split)
                .map(this::createPromotion)
                .toList());
    }

    private void addNoPromotionInventories(final Inventories inventories) {
        List<Product> products = getUniqueProducts(inventories);

        for (Product product : products) {
            Inventories productInventories = inventories.findProducts(product.getName());
            if (!hasNoPromotionInventory(productInventories)) {
                inventories.add(new Inventory(product, 0, NULL));
            }
        }
    }

    private List<Product> getUniqueProducts(final Inventories inventories) {
        return inventories.getInventories().stream()
                .map(Inventory::getProduct)
                .distinct()
                .toList();
    }

    private boolean hasNoPromotionInventory(final Inventories inventories) {
        return inventories.getInventories().stream()
                .anyMatch(Inventory::hasNoPromotion);
    }

    private List<String> readInventories() {
        List<String> inventoriesFromSource = StoreFileReader.readInventories();
        return FileContentParser.removeHeaders(inventoriesFromSource);
    }

    private Inventory createInventory(final List<String> splittedText) {
        Product product = new Product(splittedText.get(0), new BigDecimal(splittedText.get(1)));
        return new Inventory(product,
                Converter.convertToInteger(splittedText.get(2)),
                splittedText.get(3));
    }

    private List<String> readPromotions() {
        List<String> promotionsFromSource = StoreFileReader.readPromotions();
        return FileContentParser.removeHeaders(promotionsFromSource);
    }

    private Promotion createPromotion(final List<String> splittedText) {
        Quantity purchaseQuantity = new Quantity(Converter.convertToInteger(splittedText.get(1)));
        Quantity bonusQuantity = new Quantity(Converter.convertToInteger(splittedText.get(2)));
        LocalDate startDate = DateTimeParser.parseToLocalDate(splittedText.get(3));
        LocalDate endDate = DateTimeParser.parseToLocalDate(splittedText.get(4));
        return new Promotion(splittedText.get(0), purchaseQuantity, bonusQuantity, startDate, endDate);
    }
}
