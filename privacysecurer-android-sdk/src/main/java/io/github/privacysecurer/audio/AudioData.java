package io.github.privacysecurer.audio;


import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.StatisticUtils;
import io.github.privacysecurer.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An abstraction of audio data.
 */
public class AudioData {
    private final int type;
    private static final int TYPE_TEMP_RECORD = 0;
    private static final int TYPE_LOCAL_FILE = 1;
    private static final int TYPE_REMOTE_FILE = 1;

    private File audioFile;
    private List<Integer> amplitudeSamples;

    private final static double AMPLITUDE_BASE = 1.0;

    private AudioData(int type) {
        this.type = type;
    }

    static AudioData newTempRecord(File tempRecordFile, List<Integer> amplitudeSamples) {
        AudioData audioData = new AudioData(TYPE_TEMP_RECORD);
        audioData.audioFile = tempRecordFile;
        audioData.amplitudeSamples = amplitudeSamples;
        return audioData;
    }

    static AudioData newLocalAudio(File localAudioFile) {
        AudioData audioData = new AudioData(TYPE_LOCAL_FILE);
        audioData.audioFile = localAudioFile;
        return audioData;
    }

    List<Integer> getAmplitudeSamples() {
        if (this.amplitudeSamples != null) return this.amplitudeSamples;
        // TODO get amplitude samples from local file.
        return new ArrayList<>();
    }

    String getFilepath(UQI uqi) {
        if (this.audioFile != null) return this.audioFile.getAbsolutePath();
        return null;
    }

    Integer getMaxAmplitude(UQI uqi) {
        return StatisticUtils.max(this.getAmplitudeSamples());
    }

    Double getLoudness(UQI uqi) {
        return convertAmplitudeToLoudness(uqi, StatisticUtils.rms(this.getAmplitudeSamples()));
    }

    Double getMaxLoudness(UQI uqi) {
        Integer amplitude = StatisticUtils.max(this.getAmplitudeSamples());
        Double maxLoudness = convertAmplitudeToLoudness(uqi, amplitude);
        return maxLoudness;
    }

    /**
     * Developers could implement their own calculation equation here.
     * @param uqi
     * @return generated field
     */
    Double customizedFunctions(UQI uqi) {
        // Developers could write their own functions here, for example, this case is to generate minimum loudness
        Integer amplitude = StatisticUtils.min(this.getAmplitudeSamples());
        Double minLoudness = convertAmplitudeToLoudness(uqi, amplitude);
        return minLoudness;
    }

    /*Boolean checkLoudness (UQI uqi, String operator, Double threshold) {
        Double loudness = convertAmplitudeToLoudness(uqi, StatisticUtils.rms(this.getAmplitudeSamples()));
        Boolean booleanFlag = null;
        switch (operator) {
            case "gt":
                if (loudness > threshold) {
                    booleanFlag = TRUE;
                } else {
                    booleanFlag = FALSE;
                }
                break;
            case "lt":
                if (loudness < threshold) {
                    booleanFlag = TRUE;
                } else {
                    booleanFlag = FALSE;
                }
                break;
            case "gte":
                if (loudness >= threshold) {
                    booleanFlag = TRUE;
                } else {
                    booleanFlag = FALSE;
                }
                break;
            case "lte":
                if (loudness <= threshold) {
                    booleanFlag = TRUE;
                } else {
                    booleanFlag = FALSE;
                }
                break;
            case "eq":
                if (loudness == threshold) {
                    booleanFlag = TRUE;
                } else {
                    booleanFlag = FALSE;
                }
                break;
            case "ne":
                if (loudness != threshold) {
                    booleanFlag = TRUE;
                } else {
                    booleanFlag = FALSE;
                }
                break;
            default:
                Log.d("Log", "Operators don't match, please use gt, lt, gte, lte, eq, ne.");
        }
        return booleanFlag;
    }*/

    /*Boolean checkMaxLoudness (UQI uqi, Double threshold) {
        Integer amplitude = StatisticUtils.max(this.getAmplitudeSamples());
        Double loudness = convertAmplitudeToLoudness(uqi, amplitude);
        Boolean booleanFlag = null;
        if (loudness >= threshold) {
            booleanFlag = TRUE;
        } else {
            booleanFlag = FALSE;
        }
        return booleanFlag;
    }*/

    /*Boolean checkAmplitude (UQI uqi, Double threshold) {
        Integer amplitude = StatisticUtils.max(this.getAmplitudeSamples());
        Boolean booleanFlag = null;
        int mThreshold = Integer.valueOf(threshold.intValue());
        if (amplitude >= mThreshold) {
            booleanFlag = TRUE;
        } else {
            booleanFlag = FALSE;
        }
        return booleanFlag;
    }*/

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.type == TYPE_TEMP_RECORD) {
            StorageUtils.safeDelete(this.audioFile);
        }
    }

    static Double convertAmplitudeToLoudness(UQI uqi, Number amplitude) {
        if (amplitude == null) return null;
        double loudness = 20 * Math.log10(amplitude.doubleValue() / AMPLITUDE_BASE);
        return loudness;
    }

    public String toString() {
        return String.format(Locale.getDefault(), "<Audio@%d%d>", this.type, this.hashCode());
    }

    boolean hasHumanVoice(UQI uqi) {
        // TODO implement this
        return false;
    }
}
