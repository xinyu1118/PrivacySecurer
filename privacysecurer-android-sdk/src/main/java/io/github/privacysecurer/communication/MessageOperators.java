package io.github.privacysecurer.communication;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

/**
 * A helper class to access message-related operations
 */
@PSOperatorWrapper
public class MessageOperators {

    /**
     * Judge an unwanted message from contact field
     *
     * @param contactField the contact (phone number or name) of a message
     * @param sender the unwanted message (phone number or name) specified by developers
     * @return the function
     */
    public static Function<Item, Boolean> unwantedMessage(String contactField, String sender) {
        return new UnwantedMessageVerifier(contactField, sender);
    }

}
