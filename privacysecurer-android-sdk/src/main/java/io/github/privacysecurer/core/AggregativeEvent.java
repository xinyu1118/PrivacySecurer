package io.github.privacysecurer.core;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * Aggregative events, used for setting event parameters and providing processing methods.
 */
public class AggregativeEvent extends Event {

    /**
     * multiple events to be carried on 'and' operation
     */
    private List<Event> andEvents;
    /**
     * multiple events to be carried on 'or' operation
     */
    private List<Event> orEvents;
    /**
     * multiple events to be carried on 'not' operation
     */
    private List<Event> notEvents;

    Boolean andResult;
    Boolean orResult;
    Boolean notResult;
    Boolean result;

    long interval = 0;
    int runCount;

    public AggregativeEvent() {
        andResult = null;
        orResult = null;
        notResult = null;
        result = TRUE;
    }

    @Override
    public void setEventType(String eventType) {

    }

    @Override
    public String getEventType() {
        return null;
    }

    @Override
    public void setFieldName(String fieldName) {

    }

    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public void setOperator(String operator) {

    }

    @Override
    public String getOperator() {
        return null;
    }

    @Override
    public void setDuration(long duration) {

    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public void setInterval(long interval) {

    }

    @Override
    public long getInterval() {
        return 0;
    }

    @Override
    public void setNotificationResponsiveness(Integer recurrence) {

    }

    @Override
    public Integer getNotificationResponsiveness() {
        return null;
    }

    @Override
    public void setThreshold(Double threshold) {

    }

    @Override
    public Double getThreshold() {
        return null;
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
        this.andEvents = andEvents;
    }

    @Override
    public List<Event> getAndEvents() {
        return this.andEvents;
    }

    @Override
    public void or(List<Event> orEvents) {
        this.orEvents = orEvents;
    }

    @Override
    public List<Event> getOrEvents() {
        return this.orEvents;
    }

    @Override
    public void not(List<Event> notEvents) {
        this.notEvents = notEvents;
    }

    @Override
    public List<Event> getNotEvents() {
        return this.notEvents;
    }

    @Override
    public void setSatisfyCond() {

    }

    @Override
    public boolean getSatisfyCond() {
        return false;
    }

    @Override
    public void setBroadListener(BroadListener broadListener) {

    }

    @Override
    public void addOptimizationConstraints(List<List> batteryIntervalMatrix) {

    }


    @Override
    public void handle(Context context, PSCallback psCallback) {

         /*
         * Deal with logic 'and' operation for multiple events
         * A thread is run to monitor the specified event, and once it is triggered,
         * the boolean variable satisfyCond in each event is set to be true. Callbacks
         * are made use of to monitor the variable change, so that we could know that the
         * event has been triggered.
         */
        if (andEvents != null) {
            for (Event e : andEvents) {
                // To get the longest interval for all events
                if ((e.getDuration()+e.getInterval()) > interval) {
                    interval = e.getDuration()+e.getInterval();
                }

                final Event event = e;
                // To monitor the boolean variable satisfyCond change
                e.setBroadListener(new BroadListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Log", event.getEventType() + " satisfies conditions.");
                    }
                    @Override
                    public void onFail(String msg) {
                        Log.d("Log", "Receive fail response.");
                    }
                });

                // To execute the specific event
                e.handle(context, psCallback);
            }
        }

        /*
         * Deal with logic 'or' operation for multiple events, the same as 'and' operation
         */
        if (orEvents != null) {
            for (Event e : orEvents) {
                if (e.getInterval() > interval) {
                    interval = e.getInterval();
                }

                final Event event = e;
                e.setBroadListener(new BroadListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Log", event.getEventType() + " satisfies conditions.");
                    }
                    @Override
                    public void onFail(String msg) {
                        Log.d("Log", "Receive fail response.");
                    }
                });

                e.handle(context, psCallback);
            }
        }

        /*
         * Deal with logic 'not' operation for multiple events, the same as 'and' operation
         */
        if (notEvents != null) {
            for (Event e : notEvents) {
                if (e.getInterval() > interval) {
                    interval = e.getInterval();
                }

                final Event event = e;
                e.setBroadListener(new BroadListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Log", event.getEventType() + " satisfies conditions.");
                    }
                    @Override
                    public void onFail(String msg) {
                        Log.d("Log", "Receive fail response.");
                    }
                });

                e.handle(context, psCallback);
            }
        }

         /*
         * If interval equals to 0s, which means that none of all these events are periodically.
         * In this case, we set timer to wait 5s and then execute the following codes, and after
         * that we will not execute the code any more. Otherwise, we set timer to wait every few
         * seconds, this interval is the longest time of all events.
         */
        if (interval == 0) {
            final Handler handler = new Handler();
            // global variable, used to indicate whether it's executed for the first time
            runCount = 0;
            Runnable runnable = new Runnable(){
                @Override
                public void run() {
                    if (runCount == 1) {
                        // To be executed codes

                        // If it's the first time to be executed, stop the timer
                        handler.removeCallbacks(this);
                    }

                    if (andEvents.size() != 0) {
                        andResult = andEvents.get(0).getSatisfyCond();
                        for (int i = 1; i < andEvents.size(); i++) {
                            //Log.d("Log", "and operations:");
                            andResult = andResult && andEvents.get(i).getSatisfyCond();
                            //Log.d("Log", String.valueOf(andResult));
                        }
                        result = result && andResult;
                    }

                    if (orEvents.size() != 0) {
                        orResult = orEvents.get(0).getSatisfyCond();
                        for (int i = 1; i < orEvents.size(); i++) {
                            //Log.d("Log", "or operations:");
                            orResult = orResult || orEvents.get(i).getSatisfyCond();
                            //Log.d("Log", String.valueOf(orResult));
                        }
                        result = result && orResult;
                    }

                    if (notEvents.size() != 0) {
                        notResult = !(notEvents.get(0).getSatisfyCond());
                        for (int i = 1; i < notEvents.size(); i++) {
                            //Log.d("Log", "or operations:");
                            notResult = notResult && !(notEvents.get(i).getSatisfyCond());
                            //Log.d("Log", String.valueOf(notResult));
                        }
                        result = result && notResult;
                    }

                    if (result) {
                        Log.d("Log", "Multiple events happen simultaneously.");
                    } else {
                        Log.d("Log", "Not all events happen simultaneously.");
                    }

                    handler.postDelayed(this, 5000);
                    runCount++;
                }
            };
            // Open the timer and execute operations
            handler.postDelayed(runnable, 5000);

        } else {
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    // Don't use 'if (andEvents != null)' as condition judgement statements
                    // Although the list size equals to 0, but it's not null
                    if (andEvents != null) {
                            andResult = andEvents.get(0).getSatisfyCond();
                            for (int i = 1; i < andEvents.size(); i++) {
                                //Log.d("Log", "and operations:");
                                andResult = andResult && andEvents.get(i).getSatisfyCond();
                                //Log.d("Log", String.valueOf(andResult));
                            }
                            result = result && andResult;
                    }

                    if (orEvents != null) {
                        orResult = orEvents.get(0).getSatisfyCond();
                        for (int i = 1; i < orEvents.size(); i++) {
                            //Log.d("Log", "or operations:");
                            orResult = orResult || orEvents.get(i).getSatisfyCond();
                            //Log.d("Log", String.valueOf(orResult));
                        }
                        result = result && orResult;
                    }

                    if (notEvents != null) {
                        notResult = !(notEvents.get(0).getSatisfyCond());
                        for (int i = 1; i < notEvents.size(); i++) {
                            //Log.d("Log", "or operations:");
                            notResult = notResult && !(notEvents.get(i).getSatisfyCond());
                            //Log.d("Log", String.valueOf(notResult));
                        }
                        result = result && notResult;
                    }

                    if (result) {
                        Log.d("Log", "Multiple events happen simultaneously.");
                    } else {
                        Log.d("Log", "Not all events happen simultaneously.");
                    }

                    handler.postDelayed(this, interval);
                }
            };
            // Open the timer and execute operations
            handler.postDelayed(runnable, interval);
            //handler.removeCallbacks(this);// Stop timer
        }

    }

    /**
     * Builder pattern used to construct aggregative related events.
     */
    public static class AggregativeEventBuilder {
        private List<Event> andEvents = new ArrayList<>();
        private List<Event> orEvents = new ArrayList<>();
        private List<Event> notEvents = new ArrayList<>();

        public AggregativeEventBuilder and(Event andEvent) {
            andEvents.add(andEvent);
            return this;
        }

        public AggregativeEventBuilder or(Event orEvent) {
            orEvents.add(orEvent);
            return this;
        }

        public AggregativeEventBuilder not(Event notEvent) {
            notEvents.add(notEvent);
            return this;
        }

        public Event build() {
            AggregativeEvent aggregativeEvent = new AggregativeEvent();

            if (andEvents.size() != 0) {
                aggregativeEvent.and(andEvents);
            }

            if (orEvents.size() != 0) {
                aggregativeEvent.or(orEvents);
            }

            if (notEvents.size() != 0) {
                aggregativeEvent.or(notEvents);
            }

            return aggregativeEvent;
        }
    }

}
