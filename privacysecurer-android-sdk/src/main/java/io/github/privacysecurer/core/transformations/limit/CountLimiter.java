package io.github.privacysecurer.core.transformations.limit;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.Logging;

/**
 * Limit the count of items in the stream.
 */
final class CountLimiter extends StreamLimiter {
    private final int maxCount;
    private int count;

    CountLimiter(int maxCount) {
        this.maxCount = maxCount;
        this.count = 0;
        this.addParameters(maxCount);
    }

    @Override
    protected final void onInput(Item item) {
        if (item.isEndOfStream()) {
            this.finish();
            return;
        }
        this.count++;
        if (this.count < this.maxCount) this.output(item);
        else if (this.count == this.maxCount) {
            this.output(item);
            this.cancel(this.getUQI());
        }
        else {
            Logging.warn("CountLimiter: shouldn't be here!");
        }
    }

    @Override
    protected boolean keep(Item item) {
        return true;
    }
}
