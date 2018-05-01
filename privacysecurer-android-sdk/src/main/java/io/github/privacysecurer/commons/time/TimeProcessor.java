package io.github.privacysecurer.commons.time;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Process the timestamp specified by a field in an item.
 */
abstract class TimeProcessor<Tout> extends ItemOperator<Tout> {

    private final String timestampField;

    TimeProcessor(String timestampField) {
        this.timestampField = Assertions.notNull("timestampField", timestampField);
        this.addParameters(timestampField);
    }

    @Override
    public final Tout apply(UQI uqi, Item input) {
        long timestamp = input.getValueByField(this.timestampField);
        return this.processTimestamp(timestamp);
    }

    protected abstract Tout processTimestamp(long timestamp);
}
