package io.github.privacysecurer.core;


/**
 * Geolocation related callbacks with intermediate data.
 */
public abstract class GeolocationCallback extends EventCallback {
    private GeolocationCallbackData geolocationCallbackData;


    @Override
    public void setAudioCallbackData(AudioCallbackData audioCallbackData) {

    }

    @Override
    public AudioCallbackData getAudioCallbackData() {
        return null;
    }

    @Override
    public void setGeolocationCallbackData(GeolocationCallbackData geolocationCallbackData) {
        this.geolocationCallbackData = geolocationCallbackData;
    }

    @Override
    public GeolocationCallbackData getGeolocationCallbackData() {
        return this.geolocationCallbackData;
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

    }

    @Override
    public MessageCallbackData getMessageCallbackData() {
        return null;
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
    public void onEvent(ContactCallbackData contactCallbackData) {

    }

    @Override
    public void onEvent(MessageCallbackData messageCallbackData) {

    }

    @Override
    public void onEvent(ImageCallbackData imageCallbackData) {

    }

}
