package io.github.privacysecurer.core.actions.collect;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStream;
import io.github.privacysecurer.core.UQI;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A function that outputs a multi-item stream.
 */

class MStreamToItemListCollector extends Function<PStream, List<Item>> {

    MStreamToItemListCollector() {
    }

    private transient List<Item> items;
    private transient volatile boolean isFinished;

    @Override
    public List<Item> apply(UQI uqi, PStream input) {
        this.items = new ArrayList<>();
        this.isFinished = false;

        input.register(this);

        while (!this.isFinished && !this.isCancelled) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        input.unregister(this);
        return this.items;
    }

    @Subscribe
    public final void onEvent(Item item) {
        if (this.isCancelled) return;
        this.onInput(item);
    }

    protected void onInput(Item item) {
        if (item.isEndOfStream()) {
            this.isFinished = true;
        }
        else {
            this.items.add(item);
        }
    }
}
