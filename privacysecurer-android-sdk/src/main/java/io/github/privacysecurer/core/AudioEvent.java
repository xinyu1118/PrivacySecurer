package io.github.privacysecurer.core;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

import io.github.privacysecurer.audio.Audio;
import io.github.privacysecurer.audio.AudioOperators;
import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Audio related events, used for setting event parameters and providing processing methods.
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
     * The occurrence times for periodic events, e.g. in the event to check average audio loudness,
     * setRecurrence(2) means that if the average loudness is higher than the threshold twice, the
     * API will stop monitoring the event.
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
     * The interval of audio recording in low battery level.
     */
    private long lobatInterval;
    /**
     * The upper bound of the section in low battery level.
     */
    private int upperBound;
    /**
     * The lower bound of the section in low battery level.
     */
    private int lowerBound;
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

    private static Object monitor = new Object();
    private static boolean isCharged = false;
    private boolean broadcastRegistered = false;
    BroadcastReceiver receiver;
    Context mContext;


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
    public void addPowerConstraints(long lobatInterval, int upperBound, int lowerBound) {
        this.lobatInterval = lobatInterval;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    @Override
    public void addPrecisionConstraints(String lobatPrecision) {

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handle(Context context, final PSCallback psCallback) {
        UQI uqi = new UQI(context);
        mContext = context;

        // Judge event type
        switch (fieldName) {
            case AvgLoudness:
                if (interval == -1)
                    this.setEventType(Event.Audio_Check_Average_Loudness);
                else
                    this.setEventType(Event.Audio_Check_Average_Loudness_Periodically);
                break;
            case MaxLoudness:
                if (interval == -1)
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
                    avgLoudness = uqi.getData(Audio.record(duration), Purpose.UTILITY("Listen to average audio loudness once."))
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

                BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
                int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                // add power constrains
                if (lobatInterval != 0) {
                    // when in low battery level, enlarge the data sampling interval
                    if (batteryLevel >= lowerBound && batteryLevel < upperBound) {
                        interval = lobatInterval;
                    }

                    // when in extremely low battery level, sleep until charged
                    if (batteryLevel < lowerBound) {
                        // get current charging status
                        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = context.registerReceiver(null, intentFilter);
                        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                status == BatteryManager.BATTERY_STATUS_FULL;

                        // if the device is charging, just sample data immediately, otherwise
                        // sleep until it is charged.
                        if (!isCharging) {
                            new WaitThread().start();
                            new NotifyThread().start();
                        }
                    }
                }

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

                if (broadcastRegistered)
                    mContext.unregisterReceiver(receiver);

                break;

            case Event.Audio_Check_Maximum_Loudness:
                periodicEvent = false;

                try {
                    maxLoudness = uqi.getData(Audio.record(duration), Purpose.UTILITY("Listen to audio maximum loudness once."))
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

                BatteryManager bm2 = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
                int batteryLevel2 = bm2.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                // add power constrains
                if (lobatInterval != 0) {
                    // when in low battery level, enlarge the data sampling interval
                    if (batteryLevel2 >= lowerBound && batteryLevel2 < upperBound) {
                        interval = lobatInterval;
                    }

                    // when in extremely low battery level, sleep until charged.
                    if (batteryLevel2 < lowerBound) {
                        // get current charging status
                        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = context.registerReceiver(null, intentFilter);
                        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                status == BatteryManager.BATTERY_STATUS_FULL;

                        // if the device is charging, just sample data immediately, otherwise
                        // sleep until it is charged.
                        if (!isCharging) {
                            new WaitThread().start();
                            new NotifyThread().start();
                        }
                    }
                }

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

                if (broadcastRegistered)
                    mContext.unregisterReceiver(receiver);

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
        private long lobatInterval;
        private int upperBound;
        private int lowerBound;
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

        public AudioEventBuilder addPowerConstraints(long lobatInterval, int upperBound, int lowerBound) {
            this.lobatInterval = lobatInterval;
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
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

            if (lobatInterval != 0 && upperBound != 0 && lowerBound != 0) {
                audioEvent.addPowerConstraints(lobatInterval, upperBound, lowerBound);
            }

            if (recurrence != null) {
                audioEvent.setRecurrence(recurrence);
            }

            return audioEvent;
        }
    }

    // If the device is not charged, wait the thread until power is connected.
    public class WaitThread extends Thread {
        public void run() {
            while(!isCharged) {
                synchronized(monitor) {
                    try {
                        monitor.wait();
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Notify the thread to keep on executing subsequent codes once charged.
    public class NotifyThread extends Thread {
        public void run() {
            IntentFilter ifilter = new IntentFilter();
            ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
            //ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                        //Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                        isCharged = true;
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }

                }
            };
            mContext.registerReceiver(receiver, ifilter);
            broadcastRegistered = true;
        }
    }

}
