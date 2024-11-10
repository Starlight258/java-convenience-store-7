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

    public void noteAddingMoreQuantity(final Product product, final Quantity bonusQuantity,
                                       final Quantity canGetMoreQuantity) {
        noteBonusProduct(product, bonusQuantity);
        notePurchaseProduct(product, canGetMoreQuantity);
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public Membership getMembership() {
        return membership;
    }
}
