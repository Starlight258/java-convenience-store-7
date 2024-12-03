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

    public String readRegularPayment() {
        return readLine();
    }

    public String readBenefitAnswer() {
        return readLine();
    }

    public String readMembershipAnswer() {
        return readLine();
    }

    public String readRetryAnswer() {
        return readLine();
    }
}
