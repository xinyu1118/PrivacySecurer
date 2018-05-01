package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;

/**
 * Get the file path of the audio in an AudioData field.
 */
class AudioFilepathGetter extends AudioProcessor<String> {

    AudioFilepathGetter(String photoField) {
        super(photoField);
    }

    @Override
    protected String processAudio(UQI uqi, AudioData audioData) {
        return audioData.getFilepath(uqi);
    }

}
