package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.UQI;


/**
 * The class is used to generate customized field values for audio data.
 */
class CustomizedFieldGenerator extends AudioProcessor<Double> {

    CustomizedFieldGenerator(String audioDataField) {
        super(audioDataField);
    }

    @Override
    protected Double processAudio(UQI uqi, AudioData audioData) {
        return audioData.customizedFunctions(uqi);
    }

}
