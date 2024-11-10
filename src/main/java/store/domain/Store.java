package store.domain;

import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.quantity.Quantity;
import store.domain.receipt.Receipt;

public class Store {

    final Receipt receipt;
    final Membership membership;

    public Store(final Receipt receipt, final Membership membership) {
        this.receipt = receipt;
        this.membership = membership;
    }

    public void noteNoPromotionProduct(final Product product, final Quantity totalQuantity) {
        receipt.purchaseProducts(product, totalQuantity);
        membership.addNoPromotionProduct(product, totalQuantity);
    }

    public void notePurchaseProduct(final Product product, final Quantity quantity) {
        receipt.purchaseProducts(product, quantity);
    }

    public void noteBonusProduct(final Product product, final Quantity bonusQuantity) {
        receipt.addBonusProducts(product, bonusQuantity);
    }
}
