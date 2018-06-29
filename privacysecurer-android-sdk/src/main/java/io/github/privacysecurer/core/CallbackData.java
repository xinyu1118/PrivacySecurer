package io.github.privacysecurer.core;


/**
 * Abstract callback data class, implemented by subclasses AudioCallbackData, GeolocationCallbackData,
 * ContactCallbackData, MessageCallbackData, ImageCallbackData.
 */
public abstract class CallbackData {
    /**
     * The time in milliseconds when the callback data is generated, it's a general field for all personal data callbacks.
     */
    public long TIME_CREATED;
    /**
     * Event type description, it's a general field for all personal data callbacks.
     */
    public String EVENT_TYPE;

}
