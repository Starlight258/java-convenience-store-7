package store.domain;

import java.util.ArrayList;
import java.util.List;
import store.domain.inventory.Product;
import store.domain.price.Price;
import store.domain.quantity.Quantity;

public class PurchaseContext {

    private final List<Item> purchaseItems;
    private final List<Item> bonusItems;
    private Price membershipPrice;

    public PurchaseContext() {
        this.purchaseItems = new ArrayList<>();
        this.bonusItems = new ArrayList<>();
        this.membershipPrice = Price.zero();
    }

    public void addPurchaseItems(Product product, Quantity quantity) {
        purchaseItems.add(new Item(product, quantity));
    }

    public void addBonusItems(Product product, Quantity quantity) {
        bonusItems.add(new Item(product, quantity));
    }

    public void addMembershipPrice(Price price) {
        membershipPrice = membershipPrice.add(price);
    }

    private record Item(Product product, Quantity quantity) {
    }
}
