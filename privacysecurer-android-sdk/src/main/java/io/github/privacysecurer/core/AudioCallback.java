package io.github.privacysecurer.core;


/**
 * Audio related callbacks with intermediate data.
 */
public abstract class AudioCallback extends EventCallback {
    private AudioCallbackData audioCallbackData;


    @Override
    public void setAudioCallbackData(AudioCallbackData audioCallbackData) {
        this.audioCallbackData = audioCallbackData;
    }

    @Override
    public AudioCallbackData getAudioCallbackData() {
        return this.audioCallbackData;
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

    }

    @Override
    public ImageCallbackData getImageCallbackData() {
        return null;
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

    @Override
    public void onEvent(ImageCallbackData imageCallbackData) {

    }

}
