package store.support;

import java.util.List;

public class StoreSplitter {

    public static final int CONTAINS_EMPTY = -1;

    private final String delimiter;

    public StoreSplitter(final String delimiter) {
        this.delimiter = delimiter;
    }

    public List<String> split(String text) {
        return List.of(text.split(delimiter, CONTAINS_EMPTY));
    }
}
