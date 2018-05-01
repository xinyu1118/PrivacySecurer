package io.github.privacysecurer.core.transformations.map;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamTransformation;

import static io.github.privacysecurer.utils.Assertions.notNull;

/**
 * Make the items be sent in a fixed interval.
 */
final class FixedIntervalMapper extends PStreamTransformation {

    private final long fixedInterval;

    FixedIntervalMapper(final long fixedInterval) {
        this.fixedInterval = fixedInterval;
        this.addParameters(fixedInterval);
    }

    private transient Timer timer;
    private transient Item lastItem;

    /*
     * Start the timer when the function starts evaluating
     */
    protected void init() {
        super.init();
        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                if (lastItem != null)
                    output(lastItem);
            }
        };
        timer = new Timer();
        timer.schedule(timertask, 0, fixedInterval);
    }

    @Override
    protected final void onInput(Item item) {
        if (item.isEndOfStream()) {
            timer.cancel();
            this.finish();
            return;
        }
        lastItem = item;
    }
}
