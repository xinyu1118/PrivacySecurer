package io.github.privacysecurer.core.actions.callback;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamAction;
import io.github.privacysecurer.utils.Assertions;

/**
 * Callback with an item if the item is different from the former one.
 */

class OnChangeCallback extends PStreamAction {
    private final Function<Item, Void> itemCallback;

    OnChangeCallback(Function<Item, Void> itemCallback) {
        this.itemCallback = Assertions.notNull("itemCallback", itemCallback);
        this.addParameters(itemCallback);
    }

    private transient Item lastItem;
    @Override
    protected void onInput(Item item) {
        if (item.isEndOfStream()) {
            this.finish();
            return;
        }
        if (item.equals(lastItem)) return;
        this.itemCallback.apply(this.getUQI(), item);
        this.lastItem = item;
    }

}
