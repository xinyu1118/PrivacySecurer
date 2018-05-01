package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;

/**
 * Detect human voice from the audio.
 * Return true if there is human voice.
 */

class AudioVoiceDetector extends AudioProcessor<Boolean> {

    AudioVoiceDetector(String audioDataField) {
        super(audioDataField);
    }

    @Override
    protected Boolean processAudio(UQI uqi, AudioData audioData) {
        return audioData.hasHumanVoice(uqi);
    }

}
