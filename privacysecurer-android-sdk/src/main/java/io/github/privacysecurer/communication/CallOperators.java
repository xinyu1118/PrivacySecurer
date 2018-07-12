package io.github.privacysecurer.communication;


import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

/**
 * A helper class to access call-related operations.
 */
@PSOperatorWrapper
public class CallOperators {

    /**
     * Get the phone number from incoming calls.
     *
     * @return the function
     */
    public static Function<Item, String> callerIdentification() {
        String contactField = Call.CONTACT;
        return new CallNumberGetter(contactField);
    }

}