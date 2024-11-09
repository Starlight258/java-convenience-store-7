package store.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Parser {

    public static LocalDate parseToLocalDate(final String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("[ERROR] 유효한 날짜형식이 아닙니다.");
        }
    }
}
