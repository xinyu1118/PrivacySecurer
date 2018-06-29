package io.github.privacysecurer.core;


/**
 * Image related callbacks with intermediate data.
 */
public abstract class ImageCallback extends EventCallback {
    private ImageCallbackData imageCallbackData;


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

    }

    @Override
    public MessageCallbackData getMessageCallbackData() {
        return null;
    }

    @Override
    public void setImageCallbackData(ImageCallbackData imageCallbackData) {
        this.imageCallbackData = imageCallbackData;
    }

    @Override
    public ImageCallbackData getImageCallbackData() {
        return this.imageCallbackData;
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
    public void onEvent(MessageCallbackData messageCallbackData) {

    }

}
