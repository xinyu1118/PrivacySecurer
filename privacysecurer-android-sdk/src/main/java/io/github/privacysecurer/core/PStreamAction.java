package io.github.privacysecurer.core;

import io.github.privacysecurer.core.EventDrivenFunction;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStream;
import io.github.privacysecurer.core.Stream;
import io.github.privacysecurer.core.UQI;

import org.greenrobot.eventbus.Subscribe;

/**
 * Output a stream.
 */

public abstract class PStreamAction extends EventDrivenFunction<PStream, Void> {

    protected abstract void onInput(Item item);

    @Subscribe
    public final void onEvent(Item item) {
        if (this.isCancelled || item == null) return;
        this.onInput(item);
    }

    @Override
    protected final void init() {
        this.input.register(this);
    }

    protected final void finish() {
        this.input.unregister(this);
    }

    @Override
    protected final void onCancel(UQI uqi) {
        super.onCancel(uqi);
        if (this.input != null)
            this.input.unregister(this);
    }
}
