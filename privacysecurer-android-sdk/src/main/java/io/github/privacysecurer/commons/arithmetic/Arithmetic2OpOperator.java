package io.github.privacysecurer.commons.arithmetic;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Process the number specified by a field in an item.
 */
abstract class Arithmetic2OpOperator<Tout> extends ItemOperator<Tout> {

    private final String numField1;
    private final String numField2;

    Arithmetic2OpOperator(String numField1, String numField2) {
        this.numField1 = Assertions.notNull("numField1", numField1);
        this.numField2 = Assertions.notNull("numField2", numField2);
        this.addParameters(numField1, numField2);
    }

    @Override
    public final Tout apply(UQI uqi, Item input) {
        Number number1 = input.getValueByField(this.numField1);
        Number number2 = input.getValueByField(this.numField2);
        return this.processNums(number1, number2);
    }

    protected abstract Tout processNums(Number number1, Number number2);
}
