package io.github.privacysecurer.communication;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

/**
 * A helper class to access call-related operations
 */
@PSOperatorWrapper
public class CallOperators {
    /**
     * Judge an unwanted call by contact field from call logs
     *
     * @param contactField the contact (phone number or name) of a phone call
     * @param caller the unwanted call (phone number or name) specified by developers
     * @return the function
     */
    public static Function<Item, Boolean> unwantedCall(String contactField, String caller) {
        return new UnwantedCallVerifier(contactField, caller);
    }
}