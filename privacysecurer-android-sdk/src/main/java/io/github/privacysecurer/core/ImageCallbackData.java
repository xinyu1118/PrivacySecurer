package io.github.privacysecurer.core;


/**
 * Image callback data containing several fine-grained fields.
 */
public class ImageCallbackData extends CallbackData {

    public ImageCallbackData() {
        this.TIME_CREATED = System.currentTimeMillis();
    }

    public void setEventType(String eventType) {
        this.EVENT_TYPE = eventType;
    }

}
