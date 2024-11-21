package store.util;

import java.util.List;
import store.domain.factory.StoreFactory;
import store.domain.inventory.Inventories;
import store.domain.promotion.Promotions;

public class StoreInitializer {

    private final StoreDataLoader storeDataLoader;
    private final StoreFactory storeFactory;

    public StoreInitializer(final StoreDataLoader storeDataLoader, final StoreFactory storeFactory) {
        this.storeDataLoader = storeDataLoader;
        this.storeFactory = storeFactory;
    }

    public Inventories loadInventories() {
        List<String> inventories = storeDataLoader.readInventories();
        return storeFactory.createInventories(inventories);
    }

    public Promotions loadPromotions() {
        List<String> promotions = storeDataLoader.readPromotions();
        return storeFactory.createPromotions(promotions);
    }
}
