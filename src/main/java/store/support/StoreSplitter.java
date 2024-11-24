package store.support;

import java.util.List;
import store.util.InputValidator;

public class StoreSplitter {

    public static final int CONTAINS_EMPTY = -1;

    private final String delimiter;

    public StoreSplitter(final String delimiter) {
        this.delimiter = delimiter;
    }

    public List<String> split(String text) {
        InputValidator.validateNotNullOrBlank(text);
        return List.of(text.split(delimiter, CONTAINS_EMPTY));
    }
}
