package io.github.privacysecurer.commons.item;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;

import static io.github.privacysecurer.utils.Assertions.notNull;

/**
 * A function that gets the value of a field
 */

class FieldValueGetter<TValue> extends ItemOperator<TValue> {
    private final String fieldToGet;

    FieldValueGetter(String fieldToGet) {
        this.fieldToGet = notNull("fieldToGet", fieldToGet);
        this.addParameters(fieldToGet);
    }

    @Override
    public TValue apply(UQI uqi, Item input) {
        return input.getValueByField(this.fieldToGet);
    }
}
