package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;

import java.util.List;

/**
 * Get the amplitude samples of the audio in an audio field.
 */

class AudioAmplitudeSamplesGetter extends AudioProcessor<List<Integer>> {

    AudioAmplitudeSamplesGetter(String audioDataField) {
        super(audioDataField);
    }

    @Override
    protected List<Integer> processAudio(UQI uqi, AudioData audioData) {
        return audioData.getAmplitudeSamples();
    }

}
