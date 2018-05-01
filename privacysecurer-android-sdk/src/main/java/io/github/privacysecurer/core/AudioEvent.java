package io.github.privacysecurer.core;


import android.content.Context;
import android.util.Log;

import java.util.List;

import io.github.privacysecurer.audio.Audio;
import io.github.privacysecurer.audio.AudioOperators;
import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;

/**
 * Audio related event, used for setting parameters and providing event processing methods.
 */
public class AudioEvent extends Event {
    public static final String AvgLoudness = "avgLoudness";
    public static final String MaxLoudness = "maxLoudness";

    public static final String GTE = "gte";
    public static final String LTE = "lte";
    public static final String GT = "gt";
    public static final String LT = "lt";
    public static final String EQ = "eq";
    public static final String NE = "ne";

    /**
     * The boolean flag used to indicate whether the event is triggered or not,
     * its initial value is false.
     */
    public boolean satisfyCond = false;
    private BroadListener broadListener;

    /**
     * Event type, defined in Event class.
     */
    private String eventType;
    /**
     * The occurrence times for periodic events, e.g. for the Audio_Check_Average_Loudness_Periodically event,
     * setRecurrence(2) means that if the average loudness is higher than the threshold twice, the programming model
     * will stop monitoring the event.
     */
    private Integer recurrence;
    /**
     * The field name of personal data.
     */
    private String fieldName;
    /**
     * The operators on the field value.
     */
    private String operator;
    /**
     * The duration of audio recording in milliseconds.
     */
    private long duration;
    /**
     * The interval of audio recording in milliseconds.
     */
    private long interval;
    /**
     * The threshold to be compared with average or maximum loudness.
     */
    private Double threshold;

    /**
     * Intermediate data to be returned, average loudness in dB.
     */
    private Double avgLoudness;
    /**
     * Intermediate data to be returned, maximum loudness in dB.
     */
    private Double maxLoudness;

    // used to count the event occurrence times
    int counter = 0;


    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public void setRecurrence(Integer recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public Integer getRecurrence() {
        return this.recurrence;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String getOperator() {
        return this.operator;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public long getInterval() {
        return this.interval;
    }

    @Override
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    @Override
    public Double getThreshold() {
        return this.threshold;
    }

    @Override
    public void setLocationPrecision(String locationPrecision) {

    }

    @Override
    public String getLocationPrecision() {
        return null;
    }

    @Override
    public void setLatitude(Double latitude) {

    }

    @Override
    public Double getLatitude() {
        return null;
    }

    @Override
    public void setLongitude(Double longitude) {

    }

    @Override
    public Double getLongitude() {
        return null;
    }

    @Override
    public void setRadius(Double radius) {

    }

    @Override
    public Double getRadius() {
        return null;
    }

    @Override
    public void setPlaceName(String placeName) {

    }

    @Override
    public String getPlaceName() {
        return null;
    }

    @Override
    public void setLists(List<String> lists) {

    }

    @Override
    public List<String> getLists() {
        return null;
    }

    @Override
    public void setCaller(String caller) {

    }

    @Override
    public String getCaller() {
        return null;
    }

    @Override
    public void setPath(String path) {

    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void and(List<Event> andEvents) {

    }

    @Override
    public List<Event> getAndEvents() {
        return null;
    }

    @Override
    public void or(List<Event> orEvents) {

    }

    @Override
    public List<Event> getOrEvents() {
        return null;
    }

    @Override
    public void not(List<Event> notEvents) {

    }

    @Override
    public List<Event> getNotEvents() {
        return null;
    }

    @Override
    public void setSatisfyCond() {
        this.satisfyCond = true;
        if (broadListener != null) {
            if (satisfyCond) {
                broadListener.onSuccess();
            } else {
                broadListener.onFail("Receive failed response.");
            }
        }
    }

    @Override
    public boolean getSatisfyCond() {
        return this.satisfyCond;
    }

    @Override
    public void setBroadListener(BroadListener broadListener) {
        this.broadListener = broadListener;
    }


    @Override
    public void handle(Context context, final PSCallback psCallback) {
        UQI uqi = new UQI(context);

        // Judge event type
        switch (fieldName) {
            case AvgLoudness:
                if (interval == 0)
                    this.setEventType(Event.Audio_Check_Average_Loudness);
                else
                    this.setEventType(Event.Audio_Check_Average_Loudness_Periodically);
                break;
            case MaxLoudness:
                if (interval == 0)
                    this.setEventType(Event.Audio_Check_Maximum_Loudness);
                else
                    this.setEventType(Event.Audio_Check_Maximum_Loudness_Periodically);
                break;
            default:
                Log.d("Log", "No matchable event type, please check it again.");
        }

        // Handle events according to event type
        switch (eventType) {
            case Event.Audio_Check_Average_Loudness:
                periodicEvent = false;

                try {
                    avgLoudness = uqi.getData(Audio.record(duration), Purpose.UTILITY("Listen to average audio loudness."))
                            .setField("avgLoudness", AudioOperators.calcLoudness(Audio.AUDIO_DATA))
                            .getFirst("avgLoudness");
                } catch (PSException e) {
                    e.printStackTrace();
                }

                switch (operator) {
                    case GTE:
                        if (avgLoudness >= threshold) {
                            Log.d("Log", "Average loudness is greater than or equal to the threshold.");
                            setSatisfyCond();
                            //Toast.makeText(context, " Average loudness is higher than the threshold. ", Toast.LENGTH_SHORT).show();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case LTE:
                        if (avgLoudness <= threshold) {
                            Log.d("Log", "Average loudness is lower than or equal to the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case GT:
                        if (avgLoudness > threshold) {
                            Log.d("Log", "Average loudness is greater than the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case LT:
                        if (avgLoudness < threshold) {
                            Log.d("Log", "Average loudness is lower than the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case EQ:
                        if (avgLoudness.equals(threshold)) {
                            Log.d("Log", "Average loudness is equal to the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case NE:
                        if (!avgLoudness.equals(threshold)) {
                            Log.d("Log", "Average loudness isn't equal to the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    default:
                        Log.d("Log", "No matchable operators, please check it.");

                }
                psCallback.setAvgLoudness(avgLoudness);
                break;

            case Event.Audio_Check_Average_Loudness_Periodically:
                periodicEvent = true;

                final PStreamProvider pStreamProvider = Audio.recordPeriodic(duration, interval);
                uqi.getData(pStreamProvider, Purpose.UTILITY("Listen to average audio loudness periodically."))
                        .setField("avgLoudness", AudioOperators.calcLoudness(Audio.AUDIO_DATA))
                        .forEach("avgLoudness", new Callback<Double>() {
                            @Override
                            protected void onInput(Double avgLoudness) {
                                switch (operator) {
                                    case GTE:
                                        if (avgLoudness >= threshold) {
                                            counter ++;
                                            // Stop the monitoring thread when the event has happened recurringNumber times.
                                            if (recurrence != null && counter > recurrence) {
                                                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                                pStreamProvider.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Average loudness is greater than or equal to the threshold.");
                                                psCallback.setAvgLoudness(avgLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LTE:
                                        if (avgLoudness <= threshold) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider.isCancelled = true;
                                            else {
                                                Log.d("Log", "Average loudness is lower than or equal to the threshold.");
                                                psCallback.setAvgLoudness(avgLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case GT:
                                        if (avgLoudness > threshold) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider.isCancelled = true;
                                            else {
                                                Log.d("Log", "Average loudness is greater than the threshold.");
                                                psCallback.setAvgLoudness(avgLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LT:
                                        if (avgLoudness < threshold) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider.isCancelled = true;
                                            else {
                                                Log.d("Log", "Average loudness is lower than the threshold.");
                                                psCallback.setAvgLoudness(avgLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case EQ:
                                        if (avgLoudness.equals(threshold)) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider.isCancelled = true;
                                            else {
                                                Log.d("Log", "Average loudness is equal to the threshold.");
                                                psCallback.setAvgLoudness(avgLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case NE:
                                        if (!avgLoudness.equals(threshold)) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider.isCancelled = true;
                                            else {
                                                Log.d("Log", "Average loudness isn't equal to the threshold.");
                                                psCallback.setAvgLoudness(avgLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    default:
                                        Log.d("Log", "No matchable operators, please check it.");
                                }


                            }
                        });
                break;

            case Event.Audio_Check_Maximum_Loudness:
                periodicEvent = false;

                try {
                    maxLoudness = uqi.getData(Audio.record(duration), Purpose.UTILITY("Listen to audio maximum loudness."))
                            .setField("maxLoudness", AudioOperators.calcMaxLoudness(Audio.AUDIO_DATA))
                            .getFirst("maxLoudness");
                } catch (PSException e) {
                    e.printStackTrace();
                }

                switch (operator) {
                    case GTE:
                        if (maxLoudness >= threshold) {
                            Log.d("Log", "Maximum loudness is greater than or equal to the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case LTE:
                        if (maxLoudness <= threshold) {
                            Log.d("Log", "Maximum loudness is lower than or equal to the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case GT:
                        if (maxLoudness > threshold) {
                            Log.d("Log", "Maximum loudness is greater than the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case LT:
                        if (maxLoudness < threshold) {
                            Log.d("Log", "Maximum loudness is lower than the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case EQ:
                        if (maxLoudness.equals(threshold)) {
                            Log.d("Log", "Maximum loudness is equal to the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    case NE:
                        if (!maxLoudness.equals(threshold)) {
                            Log.d("Log", "Maximum loudness isn't equal to the threshold.");
                            setSatisfyCond();
                        } else
                            Log.d("Log", "Event hasn't happened yet.");
                        break;

                    default:
                        Log.d("Log", "No matchable operators, please check it.");

                }

                psCallback.setMaxLoudness(maxLoudness);
                break;

            case Event.Audio_Check_Maximum_Loudness_Periodically:
                periodicEvent = true;

                final PStreamProvider pStreamProvider1 = Audio.recordPeriodic(duration, interval);
                uqi.getData(pStreamProvider1, Purpose.UTILITY("Listen to audio maximum loudness periodically."))
                        .setField("maxLoudness", AudioOperators.calcMaxLoudness(Audio.AUDIO_DATA))
                        .forEach("maxLoudness", new Callback<Double>() {
                            @Override
                            protected void onInput(Double maxLoudness) {
                                switch (operator) {
                                    case GTE:
                                        if (maxLoudness >= threshold) {
                                            counter ++;
                                            // Stop the monitoring thread when the event has happened recurringNumber times.
                                            if (recurrence != null && counter > recurrence) {
                                                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                                pStreamProvider1.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Maximum loudness is greater than or equal to the threshold.");
                                                psCallback.setMaxLoudness(maxLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LTE:
                                        if (maxLoudness <= threshold) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider1.isCancelled = true;
                                            else {
                                                Log.d("Log", "Maximum loudness is lower than or equal to the threshold.");
                                                psCallback.setMaxLoudness(maxLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case GT:
                                        if (maxLoudness > threshold) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider1.isCancelled = true;
                                            else {
                                                Log.d("Log", "Maximum loudness is greater than the threshold.");
                                                psCallback.setMaxLoudness(maxLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LT:
                                        if (maxLoudness < threshold) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider1.isCancelled = true;
                                            else {
                                                Log.d("Log", "Maximum loudness is lower than the threshold.");
                                                psCallback.setMaxLoudness(maxLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case EQ:
                                        if (maxLoudness.equals(threshold)) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider1.isCancelled = true;
                                            else {
                                                Log.d("Log", "Maximum loudness is equal to the threshold.");
                                                psCallback.setMaxLoudness(maxLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case NE:
                                        if (!maxLoudness.equals(threshold)) {
                                            counter ++;
                                            if (recurrence != null && counter > recurrence)
                                                pStreamProvider1.isCancelled = true;
                                            else {
                                                Log.d("Log", "Maximum loudness isn't equal to the threshold.");
                                                psCallback.setMaxLoudness(maxLoudness);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    default:
                                        Log.d("Log", "No matchable operators, please check it.");
                                }

                            }
                        });
                break;

            case Event.Audio_Has_Human_Voice:
                //TODO
                break;

            default:
                Log.d("Log", "No audio event matches your input, please check it.");
        }
    }

    /**
     * Inner class used to build audio related events and corresponding parameters.
     */
    public static class AudioEventBuilder {
        private String fieldName;
        private String operator;
        private Double threshold;
        private long duration;
        private long interval;
        private Integer recurrence;

        public AudioEventBuilder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public AudioEventBuilder setOperator(String operator) {
            this.operator = operator;
            return this;
        }

        public AudioEventBuilder setThreshold(Double threshold) {
            this.threshold = threshold;
            return this;
        }

        public AudioEventBuilder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public AudioEventBuilder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public AudioEventBuilder setRecurrence(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public Event build() {
            AudioEvent audioEvent = new AudioEvent();

            if (fieldName != null) {
                audioEvent.setFieldName(fieldName);
            }

            if (operator != null) {
                audioEvent.setOperator(operator);
            }

            if (threshold != null) {
                audioEvent.setThreshold(threshold);
            }

            if (duration != 0) {
                audioEvent.setDuration(duration);
            }

            if (interval != 0) {
                audioEvent.setInterval(interval);
            }

            if (recurrence != null) {
                audioEvent.setRecurrence(recurrence);
            }

            return audioEvent;
        }
    }

}
