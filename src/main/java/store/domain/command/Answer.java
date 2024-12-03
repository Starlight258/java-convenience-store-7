package store.domain.command;

import java.util.Arrays;
import store.exception.CustomIllegalArgumentException;
import store.exception.ErrorMessage;

public enum Answer {

    YES("Y"),
    NO("N");

    private final String value;

    Answer(final String value) {
        this.value = value;
    }

    public static Answer from(String input) {
        return Arrays.stream(values())
                .filter(answer -> answer.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new CustomIllegalArgumentException(ErrorMessage.INVALID_INPUT));
    }

    public boolean isYes() {
        return this == YES;
    }
}
