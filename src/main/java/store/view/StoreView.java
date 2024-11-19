package store.view;

import store.domain.inventory.Inventories;
import store.domain.inventory.Inventory;
import store.domain.price.Price;
import store.domain.receipt.Receipt;
import store.support.StoreFormatter;

public class StoreView {

    private final InputView inputView;
    private final OutputView outputView;
    private final InteractionView interactionView;
    private final StoreFormatter storeFormatter;

    public StoreView(final InputView inputView, final OutputView outputView, final InteractionView interactionView,
                     final StoreFormatter storeFormatter) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.interactionView = interactionView;
        this.storeFormatter = storeFormatter;
    }

    public void showInventories(final Inventories inventories) {
        for (Inventory inventory : inventories.getInventories()) {
            showInventory(inventory);
        }
    }

    private void showInventory(Inventory inventory) {
        outputView.showMessage(storeFormatter.makeInventoryMessage(inventory));
    }

    public void showBlankLine() {
        outputView.showBlankLine();
    }

    public void showStartMessage() {
        outputView.showStartMessage();
    }

    public void showCommentOfPurchase() {
        outputView.showCommentOfPurchase();
    }

    public String readLine() {
        return inputView.readLine();
    }

    public void showResults(final Receipt receipt, final Price membershipPrice) {
        outputView.showResults(receipt, membershipPrice);
    }

    public void showCommentOfMemberShip() {
        outputView.showCommentOfMemberShip();
    }

    public boolean readAnswer() {
        return interactionView.readAnswer();
    }

    public void showAdditionalPurchase() {
        outputView.showAdditionalPurchase();
    }
}
