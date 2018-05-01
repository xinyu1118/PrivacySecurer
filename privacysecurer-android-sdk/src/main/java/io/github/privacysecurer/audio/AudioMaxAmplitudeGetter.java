package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;

/**
 * Calculate the max amplitude of an audio field.
 */

class AudioMaxAmplitudeGetter extends AudioProcessor<Integer> {

    AudioMaxAmplitudeGetter(String audioDataField) {
        super(audioDataField);
    }

    @Override
    protected Integer processAudio(UQI uqi, AudioData audioData) {
        return audioData.getMaxAmplitude(uqi);
    }

}
