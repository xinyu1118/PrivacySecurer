package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;

/**
 * Check the average loudness of an audio field.
 * The loudness is a double number indicating the sound pressure level in dB.
 */
class CheckLoudness extends AudioProcessor<Boolean> {
    private Double value = null;
    private String operatorString = null;
    CheckLoudness(String audioDataField, String operator, Double threshold) {
        super(audioDataField);
        this.value = threshold;
        this.operatorString = operator;
    }

    @Override
    protected Boolean processAudio(UQI uqi, AudioData audioData) {
        return audioData.checkLoudness(uqi, operatorString, value);
    }

}
