package io.github.privacysecurer.location;


import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Process the location speed field in an item.
 * @param <Tout> the speed type
 */
abstract class SpeedProcessor<Tout> extends ItemOperator<Tout> {
    private final String speedField;

    SpeedProcessor(String speedField) {
        this.speedField = Assertions.notNull("speedField", speedField);
        this.addParameters(this.speedField);
    }

    @Override
    public final Tout apply(UQI uqi, Item input) {
        Float speed = input.getValueByField(this.speedField);
        return this.processSpeed(speed);
    }

    protected abstract Tout processSpeed(Float speed);
}