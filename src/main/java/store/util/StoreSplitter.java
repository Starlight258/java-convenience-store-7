package store.util;

import java.util.List;

public class StoreSplitter {

    private static final int CONTAINS_EMPTY = -1;
    private static final String DELIMITER = ",";

    private StoreSplitter() {
    }

    public static List<String> split(String text) {
        InputValidator.validateNotNullOrBlank(text);
        return List.of(text.split(DELIMITER, CONTAINS_EMPTY));
    }
}
