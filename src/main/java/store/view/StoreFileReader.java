package store.view;

import static store.exception.ExceptionMessages.INVALID_FILE_FORMAT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreFileReader {

    public List<String> readFileFromSource(String fileName) {
        try {
            return readFile(fileName);
        } catch (IOException exception) {
            throw new IllegalStateException(INVALID_FILE_FORMAT.getMessageWithPrefix());
        }
    }

    private List<String> readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<String> inputs = new ArrayList<>();
        makeInput(br, inputs);
        br.close();
        return inputs;
    }

    private void makeInput(final BufferedReader br, final List<String> inputs) throws IOException {
        while (true) {
            String input = br.readLine();
            if (isTerminated(input)) {
                break;
            }
            inputs.add(input);
        }
    }

    private boolean isTerminated(final String input) {
        return input == null;
    }
}
