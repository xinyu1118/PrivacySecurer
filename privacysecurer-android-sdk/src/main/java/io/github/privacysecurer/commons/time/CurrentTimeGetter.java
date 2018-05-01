package io.github.privacysecurer.commons.time;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.UQI;

/**
 * Generate a time tag string
 */
final class CurrentTimeGetter extends Function<Void, Long> {
    CurrentTimeGetter() {
    }

    @Override
    public Long apply(UQI uqi, Void input) {
        return System.currentTimeMillis();
    }
}
