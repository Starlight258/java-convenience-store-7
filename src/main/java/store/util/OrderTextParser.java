package store.util;

import static store.exception.ExceptionMessages.INVALID_FORMAT;
import static store.exception.ExceptionMessages.WRONG_INPUT;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.domain.quantity.Quantity;

public class OrderTextParser {

    private static final String REGEX = "^\\[((\\w*\\W*)-(\\d+))\\]$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private OrderTextParser() {
    }

    public static Map<String, Quantity> parseOrders(List<String> splitText) {
        Map<String, Quantity> orders = new LinkedHashMap<>();
        for (String text : splitText) {
            Matcher matcher = validateFormat(text);
            addOrder(orders, matcher);
        }
        return orders;
    }

    private static Matcher validateFormat(final String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(INVALID_FORMAT.getMessageWithPrefix());
        }
        return matcher;
    }

    private static void addOrder(final Map<String, Quantity> orders, final Matcher matcher) {
        String productValue = matcher.group(2);
        int quantityValue = Converter.convertToInteger((matcher.group(3)));
        if (quantityValue == 0) {
            throw new IllegalArgumentException(WRONG_INPUT.getMessageWithPrefix());
        }
        orders.put(productValue, orders.getOrDefault(productValue, Quantity.zero()).add(new Quantity(quantityValue)));
    }
}
