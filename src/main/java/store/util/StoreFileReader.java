package store.util;

import static store.exception.ErrorMessage.INVALID_FILE_FORMAT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreFileReader {

    private static final String INVENTORY_FILENAME = "src/main/resources/products.md";
    private static final String PROMOTION_FILENAME = "src/main/resources/promotions.md";

    private StoreFileReader() {
    }

    public static List<String> readInventories() {
        return readFileFromSource(INVENTORY_FILENAME);
    }

    public static List<String> readPromotions() {
        return readFileFromSource(PROMOTION_FILENAME);
    }

    private static List<String> readFileFromSource(String fileName) {
        try {
            return readFile(fileName);
        } catch (IOException exception) {
            throw new IllegalStateException(INVALID_FILE_FORMAT.getMessage());
        }
    }

    private static List<String> readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<String> inputs = new ArrayList<>();
        makeInput(br, inputs);
        br.close();
        return inputs;
    }

    private static void makeInput(final BufferedReader br, final List<String> inputs) throws IOException {
        while (true) {
            String input = br.readLine();
            if (isTerminated(input)) {
                break;
            }
            inputs.add(input);
        }
    }

    private static boolean isTerminated(final String input) {
        return input == null;
    }
}

