package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputView {

    public String readLine() {
        return Console.readLine();
    }

    public List<String> readFile(String fileName) throws IOException {
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
