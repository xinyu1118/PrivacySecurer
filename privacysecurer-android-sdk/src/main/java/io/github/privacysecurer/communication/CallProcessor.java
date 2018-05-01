package io.github.privacysecurer.communication;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Process the contact field in a Call item.
 */
abstract class CallProcessor<Tout> extends ItemOperator<Tout> {
    private final String contactField;
    private final String caller;

    CallProcessor(String contactField, String caller) {
        this.contactField = Assertions.notNull("contactField", contactField);
        this.caller = Assertions.notNull("caller", caller);
        this.addParameters(contactField, caller);
    }

    @Override
    public final Tout apply(UQI uqi, Item input) {
        String contact = input.getValueByField(this.contactField);
        return this.processCall(uqi, contact, this.caller);
    }

    protected abstract Tout processCall(UQI uqi, String contactField, String caller);
}