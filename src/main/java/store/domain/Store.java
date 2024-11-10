package store.domain;

import store.domain.inventory.Product;
import store.domain.membership.Membership;
import store.domain.receipt.Receipt;

public class Store {

    final Receipt receipt;
    final Membership membership;

    public Store(final Receipt receipt, final Membership membership) {
        this.receipt = receipt;
        this.membership = membership;
    }

    public void noteNoPromotionProduct(final Product product, final int totalQuantity) {
        receipt.purchaseProducts(product, totalQuantity);
        membership.addNoPromotionProduct(product, totalQuantity);
    }

    public void notePurchaseProduct(final Product product, final int quantity) {
        receipt.purchaseProducts(product, quantity);
    }

    public void noteBonusProduct(final Product product, final int bonusQuantity) {
        receipt.addBonusProducts(product, bonusQuantity);
    }
}
