package io.github.privacysecurer.commons.item;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * A function that is independent from current item.
 */

class IndependentItemOperator<Tout> extends ItemOperator<Tout> {
    private Function<Void, Tout> voidInFunction;

    IndependentItemOperator(Function<Void, Tout> voidInFunction) {
        this.voidInFunction = Assertions.notNull("voidInFunction", voidInFunction);
        this.addParameters(voidInFunction);
    }

    @Override
    public Tout apply(UQI uqi, Item input) {
        return this.voidInFunction.apply(uqi, null);
    }
}
