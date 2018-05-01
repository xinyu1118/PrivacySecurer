package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;

/**
 * Check the maximum loudness of an audio field.
 * The loudness is a double number indicating the sound pressure level in dB.
 */
public class CheckMaxLoudness extends AudioProcessor<Boolean> {
    private Double value = null;

    CheckMaxLoudness(String audioDataField, Double threshold) {
        super(audioDataField);
        this.value = threshold;
    }

    @Override
    protected Boolean processAudio(UQI uqi, AudioData audioData) {
        return audioData.checkMaxLoudness(uqi, value);
    }
}



