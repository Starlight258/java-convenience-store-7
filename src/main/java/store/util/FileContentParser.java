package store.util;

import java.util.Collections;
import java.util.List;

public class FileContentParser {

    private static final int HEADER_LINES = 1;

    private FileContentParser() {
    }

    public static List<String> removeHeaders(List<String> lines) {
        if (lines.size() <= HEADER_LINES) {
            return Collections.emptyList();
        }
        return lines.subList(HEADER_LINES, lines.size());
    }
}
