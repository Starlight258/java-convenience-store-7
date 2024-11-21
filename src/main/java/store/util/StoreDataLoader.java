package store.util;

import java.util.List;

public class StoreDataLoader {

    public List<String> readInventories() {
        List<String> inventoriesFromSource = StoreFileReader.readInventories();
        return FileContentParser.removeHeaders(inventoriesFromSource);
    }

    public List<String> readPromotions() {
        List<String> promotionsFromSource = StoreFileReader.readPromotions();
        return FileContentParser.removeHeaders(promotionsFromSource);
    }
}
