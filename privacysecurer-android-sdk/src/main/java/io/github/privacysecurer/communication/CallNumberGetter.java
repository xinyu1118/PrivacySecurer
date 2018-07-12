package io.github.privacysecurer.communication;


import io.github.privacysecurer.core.UQI;

/**
 * Get the phone number from incoming calls.
 */
class CallNumberGetter extends CallProcessor<String> {

    CallNumberGetter(String contactField) {
        super(contactField);
    }

    @Override
    protected String processCall(UQI uqi, String contact) {
        if (contact == null) return null;
        return contact;
    }

}
