package store.domain.factory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.inventory.Product;
import store.domain.promotion.Promotion;
import store.domain.promotion.Promotions;
import store.domain.quantity.Quantity;
import store.util.Converter;
import store.util.StoreSplitter;
import store.util.StringParser;

public class StoreFactory {

    public Inventories createInventories(final List<String> inventories) {
        Inventories fromInventories = new Inventories(inventories.stream()
                .map(StoreSplitter::split)
                .map(this::createInventory)
                .toList());
        addNoPromotionInventories(fromInventories);
        return fromInventories;
    }

    public Promotions createPromotions(final List<String> promotions) {
        return new Promotions(promotions.stream()
                .map(StoreSplitter::split)
                .map(this::createPromotion)
                .toList());
    }

    private Inventory createInventory(final List<String> text) {
        Product product = new Product(text.get(0), new BigDecimal(text.get(1)));
        return new Inventory(product, Converter.convertToInteger(text.get(2)), text.get(3));
    }

    private void addNoPromotionInventories(final Inventories inventories) {
        List<Product> products = getUniqueProducts(inventories);

        for (Product product : products) {
            Inventories productInventories = inventories.findProducts(product.getName());
            if (!hasNoPromotionInventory(productInventories)) {
                inventories.add(Inventory.createNoPromotionEmptyInventory(product));
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

    private Promotion createPromotion(final List<String> text) {
        Quantity purchaseQuantity = new Quantity(Converter.convertToInteger(text.get(1)));
        Quantity bonusQuantity = new Quantity(Converter.convertToInteger(text.get(2)));
        LocalDate startDate = StringParser.parseToLocalDate(text.get(3));
        LocalDate endDate = StringParser.parseToLocalDate(text.get(4));
        return new Promotion(text.get(0), purchaseQuantity, bonusQuantity, startDate, endDate);
    }
}
