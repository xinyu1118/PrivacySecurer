package io.github.privacysecurer.core.transformations.reorder;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * A function that reorders the items in a stream
 */
abstract class StreamReorder extends PStreamTransformation {
    protected abstract void reorder(List<Item> item);

    private transient List<Item> items;

    @Override
    protected void onInput(Item item) {
        if (this.items == null) this.items = new ArrayList<>();
        if (item.isEndOfStream()) {
            this.reorder(items);
            for (Item i : this.items) {
                this.output(i);
            }
            this.finish();
            return;
        }
        this.items.add(item);
    }
}
