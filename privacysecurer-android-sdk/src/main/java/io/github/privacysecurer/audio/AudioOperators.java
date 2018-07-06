package io.github.privacysecurer.audio;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

import java.util.List;

/**
 * A helper class to access audio-related operators
 */
@PSOperatorWrapper
public class AudioOperators {
    /**
     * Calculate the average (RMS) loudness of the audio specified by an AudioData field.
     * The loudness is an double number indicating the sound pressure level in dB.
     *
     * @param audioDataField the name of the AudioData field.
     * @return the function.
     */
    public static Function<Item, Double> calcLoudness(String audioDataField) {
        return new AudioLoudnessCalculator(audioDataField);
    }

    /**
     * Calculate the maximum loudness of the audio specified by an AudioData field.
     * The loudness is an double number indicating the sound pressure level in dB.
     *
     * @param audioDataField the name of the AudioData field.
     * @return the function.
     */
    public static Function<Item, Double> calcMaxLoudness(String audioDataField) {
        return new AudioMaxLoudnessCalculator(audioDataField);
    }

    /**
     * Calculate a customized field value of the audio specified by an AudioData field.
     *
     * @param audioDataField the name of the AudioData field.
     * @return the function.
     */
    public static Function<Item, Double> customizedFunctions(String audioDataField) {
        return new CustomizedFieldGenerator(audioDataField);
    }

    /**
     * Check the average loudness of the audio specified by an AudioData field
     * The operations could be higher than/lower than/equal to a threshold
     *
     * @param audioDataField the name of the AudioData field
     * @param operator logic operators, i.e. gt, gte, lt, lte, eq, ne
     * @param threshold the loudness threshold
     * @return the function
     */
    /*public static Function<Item, Boolean> checkLoudness(String audioDataField, String operator, Double threshold) {
        return new CheckLoudness(audioDataField, operator, threshold);
    }*/

    /**
     * Check the maximum loudness of the audio specified by an AudioData field
     * If it is higher than the threshold, true is returned otherwise false
     *
     * @param audioDataField the name of the AudioData field
     * @param threshold the loudness threshold
     * @return the function
     */
    /*public static Function<Item, Boolean> checkMaxLoudness(String audioDataField, Double threshold) {
        return new CheckMaxLoudness(audioDataField, threshold);
    }*/

    /**
     * Check the max amplitude of the audio specified by an AudioData field
     * If it is higher than the threshold, true is returned otherwise false
     *
     * @param audioDataField the name of the AudioData field
     * @param threshold the amplitude threshold
     * @return the function
     */
    /*public static Function<Item, Boolean> checkAmplitude(String audioDataField, Double threshold) {
        return new CheckAmplitude(audioDataField, threshold);
    }*/

    /**
     * Get the max amplitude of the audio specified by an AudioData field.
     * The amplitude is an Integer from 0 to 32767.
     *
     * @param audioDataField the name of the AudioData field.
     * @return the function.
     */
    public static Function<Item, Integer> getMaxAmplitude(String audioDataField) {
        return new AudioMaxAmplitudeGetter(audioDataField);
    }

    /**
     * Get the amplitude samples of the audio specified by an AudioData field.
     * Each amplitude sample is an Integer from 0 to 32767.
     * The amplitude sampling rate can be configured at `Globals.AudioConfig.amplitudeSamplingRate`;
     *
     * @param audioDataField the name of the AudioData field.
     * @return the function.
     */
    public static Function<Item, List<Integer>> getAmplitudeSamples(String audioDataField) {
        return new AudioAmplitudeSamplesGetter(audioDataField);
    }

    /**
     * Calculate loudness (in decibels) based on an amplitude value.
     * The amplitude should be a number from 0 to 32767.
     *
     * @param amplitudeField the name of the amplitude field.
     * @return the function.
     */
    public static Function<Item, Double> convertAmplitudeToLoudness(String amplitudeField) {
        return new AmplitudeToLoudnessConverter(amplitudeField);
    }

    /**
     * Get the file path of the audio specified by an AudioData field.
     * The path might point to a temporary audio file if it is not from storage.
     * To permanently save the file, you need to copy the file to another file path.
     *
     * @param audioDataField the name of AudioData field
     * @return the function
     */
    public static Function<Item, String> getFilepath(String audioDataField) {
        return new AudioFilepathGetter(audioDataField);
    }

    /**
     * Detect human voice from the audio.
     * Return true if there is human voice.
     *
     * @param audioDataField the name of AudioData field
     * @return the function
     */
    public static Function<Item, Boolean> hasHumanVoice(String audioDataField) {
        return new AudioVoiceDetector(audioDataField);
    }

}
