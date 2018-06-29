package io.github.privacysecurer.core;


/**
 * Message related callbacks with intermediate data.
 */
public abstract class MessageCallback extends EventCallback {
    private MessageCallbackData messageCallbackData;


    @Override
    public void setAudioCallbackData(AudioCallbackData audioCallbackData) {

    }

    @Override
    public AudioCallbackData getAudioCallbackData() {
        return null;
    }

    @Override
    public void setGeolocationCallbackData(GeolocationCallbackData geolocationCallbackData) {

    }

    @Override
    public GeolocationCallbackData getGeolocationCallbackData() {
        return null;
    }

    @Override
    public void setContactCallbackData(ContactCallbackData contactCallbackData) {

    }

    @Override
    public ContactCallbackData getContactCallbackData() {
        return null;
    }

    @Override
    public void setMessageCallbackData(MessageCallbackData messageCallbackData) {
        this.messageCallbackData = messageCallbackData;
    }

    @Override
    public MessageCallbackData getMessageCallbackData() {
        return this.messageCallbackData;
    }

    @Override
    public void setImageCallbackData(ImageCallbackData imageCallbackData) {

    }

    @Override
    public ImageCallbackData getImageCallbackData() {
        return null;
    }

    @Override
    public void onEvent(AudioCallbackData audioCallbackData) {

    }

    @Override
    public void onEvent(GeolocationCallbackData geolocationCallbackData) {

    }

    @Override
    public void onEvent(ContactCallbackData contactCallbackData) {

    }

    @Override
    public void onEvent(ImageCallbackData imageCallbackData) {

    }
}
