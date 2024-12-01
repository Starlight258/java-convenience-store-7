package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import store.util.StringParser;

public class InputView {
    public List<String> readOrder() {
        return StringParser.parseByDelimiter(readLine(), ",");
    }

    private String readLine() {
        return Console.readLine();
    }
}
