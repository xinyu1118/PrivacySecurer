package io.github.privacysecurer.communication;

import io.github.privacysecurer.core.UQI;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * A class to check unwanted calls from contact field.
 */
public class UnwantedCallVerifier extends CallProcessor<Boolean> {

    UnwantedCallVerifier(String contactField, String caller) {
        super(contactField, caller);
    }

    /**
     * Process unwanted call, if contact field matches the caller, return true otherwise false
     *
     * @param uqi unified query interface
     * @param contactField the contact (phone number or name) of a phone call
     * @param caller the unwanted call (phone number or name) specified by developers
     * @return Boolean value
     */
    @Override
    protected Boolean processCall(UQI uqi, String contactField, String caller) {
        if (contactField.equals(caller)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

}