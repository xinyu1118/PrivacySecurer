package io.github.privacysecurer.core;


/**
 * Message callback data containing several fine-grained fields.
 */
public class MessageCallbackData extends CallbackData {
    /**
     * Intermediate data to be called back, the caller of incoming messages.
     */
    public String caller;

    public MessageCallbackData() {
        this.TIME_CREATED = System.currentTimeMillis();
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public void setEventType(String eventType) {
        this.EVENT_TYPE = eventType;
    }

}
