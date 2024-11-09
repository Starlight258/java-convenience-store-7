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
        while (true) {
            String input = br.readLine();
            if (input == null) {
                break;
            }
            inputs.add(input);
        }
        br.close();
        return inputs;
    }
}
