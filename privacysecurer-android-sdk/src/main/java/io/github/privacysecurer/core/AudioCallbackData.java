package io.github.privacysecurer.core;


/**
 * Audio callback data containing several fine-grained fields.
 */
public class AudioCallbackData extends CallbackData {
    /**
     * Intermediate data to be called back, average loudness in dB.
     */
    public Double avgLoudness;
    /**
     * Intermediate data to be called back, maximum loudness in dB.
     */
    public Double maxLoudness;
    /**
     * Intermediate data to be called back, user defined field value.
     */
    public Double customizedField;

    public AudioCallbackData() {
        this.TIME_CREATED = System.currentTimeMillis();
    }

    public void setAvgLoudness(Double avgLoudness) {
        this.avgLoudness = avgLoudness;
    }

    public void setMaxLoudness(Double maxLoudness) {
        this.maxLoudness = maxLoudness;
    }

    public void setCustomizedField(Double customizedField) {
        this.customizedField = customizedField;
    }

    public void setEventType(String eventType) {
        this.EVENT_TYPE = eventType;
    }

}
