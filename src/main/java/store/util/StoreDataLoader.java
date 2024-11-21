package store.util;

import java.util.List;

public class StoreDataLoader {

    public List<String> readInventories() {
        List<String> inventoriesFromSource = StoreFileReader.readInventories();
        return StringParser.removeHeaders(inventoriesFromSource);
    }

    public List<String> readPromotions() {
        List<String> promotionsFromSource = StoreFileReader.readPromotions();
        return StringParser.removeHeaders(promotionsFromSource);
    }
}
