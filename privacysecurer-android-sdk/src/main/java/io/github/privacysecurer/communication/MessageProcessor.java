package io.github.privacysecurer.communication;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Process the contact field in a Message item.
 */
abstract class MessageProcessor<Tout> extends ItemOperator<Tout> {
    private final String contactField;
    private final String sender;

    MessageProcessor(String contactField, String sender) {
        this.contactField = Assertions.notNull("contactField", contactField);
        this.sender = Assertions.notNull("sender", sender);
        this.addParameters(contactField, sender);
    }

    @Override
    public final Tout apply(UQI uqi, Item input) {
        String contact = input.getValueByField(this.contactField);
        return this.processMessage(uqi, contact, this.sender);
    }

    protected abstract Tout processMessage(UQI uqi, String contactField, String sender);
}