package io.github.privacysecurer.commons.item;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;

import static io.github.privacysecurer.utils.Assertions.notNull;

/**
 * Set a value to a field in the item.
 */

class FieldSetter<TValue> extends ItemOperator<Item> {

    private final String fieldToSet;
    private final Function<Item, TValue> functionToComputeValue;

    FieldSetter(String fieldToSet, Function<Item, TValue> functionToComputeValue) {
        this.fieldToSet = notNull("fieldToSet", fieldToSet);
        this.functionToComputeValue = notNull("functionToComputeValue", functionToComputeValue);
        this.addParameters(fieldToSet, functionToComputeValue);
    }

    @Override
    public Item apply(UQI uqi, Item input) {
        input.setFieldValue(this.fieldToSet, this.functionToComputeValue.apply(uqi, input));
        return input;
    }

}
