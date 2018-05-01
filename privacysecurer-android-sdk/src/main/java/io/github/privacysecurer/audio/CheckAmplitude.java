package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;

/**
 * Check the max amplitude of an audio field.
 * The max amplitude is an integer number from 0 to 32767.
 */
public class CheckAmplitude extends AudioProcessor<Boolean>{
    private Double value = null;

    CheckAmplitude(String audioDataField, Double threshold) {
        super(audioDataField);
        this.value = threshold;
    }

    @Override
    protected Boolean processAudio(UQI uqi, AudioData audioData) {
        return audioData.checkAmplitude(uqi, value);
    }

}