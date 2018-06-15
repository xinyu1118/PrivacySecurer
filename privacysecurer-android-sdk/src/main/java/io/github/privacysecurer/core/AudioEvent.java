package io.github.privacysecurer.core;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
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

    // Field name options
    public static final String AvgLoudness = "avgLoudness";
    public static final String MaxLoudness = "maxLoudness";

    // Operator options
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
     * Event type defined in Event class.
     */
    private String eventType;
    /**
     * The field name of personal data.
     */
    private String fieldName;
    /**
     * The operator on the field value.
     */
    private String operator;
    /**
     * The threshold to be compared with average or maximum loudness.
     */
    private Double threshold;
    /**
     * The duration of audio recording in milliseconds.
     */
    private long duration;
    /**
     * The interval of audio recording in milliseconds.
     */
    private long interval;
    /**
     * The event recurrence times, could be 0 representing that events happen uninterruptedly,
     * also positive value representing that events happen limited times, especially value 1
     * meaning that events happen only once.
     */
    private Integer recurrence;
    /**
     * A matrix setting the sampling interval values in various sections, the elements in each row
     * are upper bound, lower bound and interval in turn.
     */
    private List<List> optimizationMatrix = new ArrayList<>();

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
    public void setNotificationResponsiveness(Integer recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public Integer getNotificationResponsiveness() {
        return this.recurrence;
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
    public void addOptimizationConstraints(List<List> optimizationMatrix) {
        this.optimizationMatrix = optimizationMatrix;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handle(Context context, final PSCallback psCallback) {
        UQI uqi = new UQI(context);
        mContext = context;

        // Judge event type
        switch (fieldName) {
            case AvgLoudness:
                if (recurrence == 1)
                    this.setEventType(Event.Audio_Check_Average_Loudness);
                else
                    this.setEventType(Event.Audio_Check_Average_Loudness_Periodically);
                break;
            case MaxLoudness:
                if (recurrence == 1)
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

                // Get the current battery level
                BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
                int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                // Add interval settings based on the battery level
                if (optimizationMatrix != null) {
                    for (int i=0; i<optimizationMatrix.size(); i++) {
                       if ((Integer)optimizationMatrix.get(i).get(0) >= batteryLevel &&
                               (Integer)optimizationMatrix.get(i).get(1) <= batteryLevel) {
                           if (optimizationMatrix.get(i).get(2) != Event.Off) {
                               interval = (Long)optimizationMatrix.get(i).get(2);
                           }
                           else {
                               // get current charging status
                               IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                               Intent batteryStatus = context.registerReceiver(null, intentFilter);
                               int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                               boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                       status == BatteryManager.BATTERY_STATUS_FULL;

                               // if the device is charging, just sample data immediately, otherwise
                               // sleep until it is charged.
                               if (!isCharging) {
                                   Log.d("Log", "Event will be paused until getting charged.");
                                   new WaitThread().start();
                                   new NotifyThread().start();
                               }
                           }
                           // If found a satisfied section, just break the loop. In this way,
                           // the boundary value could also be processed appropriately.
                           break;
                       }
                    }
                }

                // add power constrains
                /*if (lobatInterval != 0) {
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
                }*/

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
                                            if (recurrence != Event.ContinuousSampling && counter > recurrence) {
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

                // Get the current battery level
                BatteryManager bm2 = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
                int batteryLevel2 = bm2.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                // Add interval settings based on the battery level
                if (optimizationMatrix != null) {
                    for (int i=0; i<optimizationMatrix.size(); i++) {
                        if ((Integer)optimizationMatrix.get(i).get(0) >= batteryLevel2 &&
                                (Integer)optimizationMatrix.get(i).get(1) <= batteryLevel2) {
                            if (optimizationMatrix.get(i).get(2) != Event.Off) {
                                interval = (Long)optimizationMatrix.get(i).get(2);
                            }
                            else {
                                // get current charging status
                                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                                Intent batteryStatus = context.registerReceiver(null, intentFilter);
                                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                        status == BatteryManager.BATTERY_STATUS_FULL;

                                // if the device is charging, just sample data immediately, otherwise
                                // sleep until it is charged.
                                if (!isCharging) {
                                    Log.d("Log", "Event will be paused until getting charged.");
                                    new WaitThread().start();
                                    new NotifyThread().start();
                                }
                            }
                            // If found a satisfied section, just break the loop. In this way,
                            // the boundary value could also be processed appropriately.
                            break;
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
                                            if (recurrence != Event.ContinuousSampling && counter > recurrence) {
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
     * Builder pattern used to construct audio related events.
     */
    public static class AudioEventBuilder {
        private String fieldName;
        private String operator;
        private Double threshold;
        private long duration;
        private long interval;
        private Integer recurrence;
        List<List> optimizationMatrix = new ArrayList<>();

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

        public AudioEventBuilder setNotificationResponsiveness(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public AudioEventBuilder addOptimizationConstraints(int upperBound, int lowerBound, long intervalInSections) {
            List rowVector = new ArrayList<>();
            rowVector.add(upperBound);
            rowVector.add(lowerBound);
            rowVector.add(intervalInSections);
            optimizationMatrix.add(rowVector);
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
                audioEvent.setNotificationResponsiveness(recurrence);
            }

            if (optimizationMatrix != null) {
                audioEvent.addOptimizationConstraints(optimizationMatrix);
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

    // Notify the thread to keep on executing subsequent codes if charged.
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
