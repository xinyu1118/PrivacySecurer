package io.github.privacysecurer.core.transformations.filter;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamTransformation;

/**
 * Exclude some items from PStream
 */

abstract class StreamFilter extends PStreamTransformation {

    @Override
    protected void onInput(Item item) {
        if (item.isEndOfStream()) {
            this.finish();
            return;
        }
        if (this.keep(item)) this.output(item);
    }

    protected abstract boolean keep(Item item);
}
