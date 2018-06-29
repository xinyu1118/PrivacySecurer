package io.github.privacysecurer.core;


import java.util.List;

/**
 * Contact callback data containing several fine-grained fields.
 */
public class ContactCallbackData extends CallbackData {
    /**
     * Intermediate data to be called back, the caller of incoming calls.
     */
    public String caller;
    /**
     * Intermediate data to be called back, a list of emails
     * matched with the emails in the contacts.
     */
    public List<String> emails;
    /**
     * Intermediate data to be called back, a stream of call items
     * meeting filtering conditions.
     */
    public List<Item> callRecords;

    public ContactCallbackData() {
        this.TIME_CREATED = System.currentTimeMillis();
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public void setCallRecords(List<Item> callRecords) {
        this.callRecords = callRecords;
    }

    public void setEventType(String eventType) {
        this.EVENT_TYPE = eventType;
    }

}
