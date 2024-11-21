package store.util;

import static store.exception.ErrorMessage.INVALID_ORDER_FORMAT;
import static store.exception.ErrorMessage.INVALID_INPUT;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.domain.quantity.Quantity;
import store.exception.CustomIllegalArgumentException;
import store.exception.ErrorMessage;

public class StringParser {

    private static final int HEADER_LINES = 1;
    private static final String REGEX = "^\\[((\\w*\\W*)-(\\d+))\\]$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private StringParser() {
    }

    public static List<String> removeHeaders(List<String> lines) {
        if (lines.size() <= HEADER_LINES) {
            return Collections.emptyList();
        }
        return lines.subList(HEADER_LINES, lines.size());
    }

    public static Map<String, Quantity> parseOrders(List<String> splitText) {
        Map<String, Quantity> orders = new LinkedHashMap<>();
        for (String text : splitText) {
            Matcher matcher = validateFormat(text);
            addOrder(orders, matcher);
        }
        return orders;
    }

    public static LocalDate parseToLocalDate(final String date) {
        InputValidator.validateNotNullOrBlank(date);
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new CustomIllegalArgumentException(ErrorMessage.INVALID_DATE_FORMAT.getMessage());
        }
    }

    private static Matcher validateFormat(final String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new CustomIllegalArgumentException(INVALID_ORDER_FORMAT.getMessage());
        }
        return matcher;
    }

    private static void addOrder(final Map<String, Quantity> orders, final Matcher matcher) {
        String productValue = matcher.group(2);
        int quantityValue = Converter.convertToInteger((matcher.group(3)));
        if (quantityValue == 0) {
            throw new CustomIllegalArgumentException(INVALID_INPUT.getMessage());
        }
        orders.put(productValue, orders.getOrDefault(productValue, Quantity.zero()).add(new Quantity(quantityValue)));
    }
}
