package io.github.privacysecurer.commons.arithmetic;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Process the number field in an item.
 */
abstract class ArithmeticOperator<Tout> extends ItemOperator<Tout> {

    private final String numField;

    ArithmeticOperator(String numField) {
        this.numField = Assertions.notNull("numField", numField);
        this.addParameters(numField);
    }

    @Override
    public final Tout apply(UQI uqi, Item input) {
        Number number = input.getValueByField(this.numField);
        return this.processNum(number);
    }

    protected abstract Tout processNum(Number number);
}
