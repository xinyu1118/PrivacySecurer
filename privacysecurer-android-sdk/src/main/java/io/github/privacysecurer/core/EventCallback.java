package io.github.privacysecurer.core;


/**
 * Abstract callback class, setting and getting parameters are implemented by subclasses
 * AudioCallback, GeolocationCallback, ContactCallback, MessageCallback, ImageCallback.
 */
public abstract class EventCallback {
    public abstract void setAudioCallbackData(AudioCallbackData audioCallbackData);
    public abstract AudioCallbackData getAudioCallbackData();
    public abstract void setGeolocationCallbackData(GeolocationCallbackData geolocationCallbackData);
    public abstract GeolocationCallbackData getGeolocationCallbackData();
    public abstract void setContactCallbackData(ContactCallbackData contactCallbackData);
    public abstract ContactCallbackData getContactCallbackData();
    public abstract void setMessageCallbackData(MessageCallbackData messageCallbackData);
    public abstract MessageCallbackData getMessageCallbackData();
    public abstract void setImageCallbackData(ImageCallbackData imageCallbackData);
    public abstract ImageCallbackData getImageCallbackData();

    public abstract void onEvent(AudioCallbackData audioCallbackData);
    public abstract void onEvent(GeolocationCallbackData geolocationCallbackData);
    public abstract void onEvent(ContactCallbackData contactCallbackData);
    public abstract void onEvent(MessageCallbackData messageCallbackData);
    public abstract void onEvent(ImageCallbackData imageCallbackData);
}
