package io.github.privacysecurer.communication;

import io.github.privacysecurer.core.UQI;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * A class to check unwanted messages from contact field.
 */
public class UnwantedMessageVerifier extends MessageProcessor<Boolean> {

    UnwantedMessageVerifier(String contactField, String sender) {
        super(contactField, sender);
    }

    /**
     * Process unwanted message, if contact field matches the sender, return true otherwise false
     *
     * @param uqi unified query interface
     * @param contactField the contact (phone number or name) of a message
     * @param sender the unwanted message (phone number or name) specified by developers
     * @return Boolean value
     */
    @Override
    protected Boolean processMessage(UQI uqi, String contactField, String sender) {
        if (contactField.equals(sender)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

}