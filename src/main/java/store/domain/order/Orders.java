package store.domain.order;

import static store.exception.ErrorMessage.INVALID_INPUT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import store.exception.CustomIllegalArgumentException;

public class Orders {

    private final List<Order> orders;

    public Orders(final List<Order> orders) {
        validate(orders);
        this.orders = new ArrayList<>(orders);
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    private void validate(final List<Order> orders) {
        int distinctSize = (int) orders.stream()
                .distinct()
                .count();
        if (distinctSize != orders.size()) {
            throw new CustomIllegalArgumentException(INVALID_INPUT);
        }
    }
}
