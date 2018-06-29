package io.github.privacysecurer.core;

/**
 * Multiple events callbacks with intermediate data.
 */
public abstract class EventCollectionCallback extends EventCallback {
    private AudioCallbackData audioCallbackData;
    private GeolocationCallbackData geolocationCallbackData;
    private ContactCallbackData contactCallbackData;
    private MessageCallbackData messageCallbackData;
    private ImageCallbackData imageCallbackData;


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
        this.geolocationCallbackData = geolocationCallbackData;
    }

    @Override
    public GeolocationCallbackData getGeolocationCallbackData() {
        return this.geolocationCallbackData;
    }

    @Override
    public void setContactCallbackData(ContactCallbackData contactCallbackData) {
        this.contactCallbackData = contactCallbackData;
    }

    @Override
    public ContactCallbackData getContactCallbackData() {
        return this.contactCallbackData;
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
        this.imageCallbackData = imageCallbackData;
    }

    @Override
    public ImageCallbackData getImageCallbackData() {
        return this.imageCallbackData;
    }

}
