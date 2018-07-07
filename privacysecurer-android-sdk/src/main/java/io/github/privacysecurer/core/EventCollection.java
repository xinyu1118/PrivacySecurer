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
public class EventCollection extends EventType {
    private String eventType;

    /**
     * multiple events to be carried on 'and' operation
     */
    private List<EventType> andEvents;
    /**
     * multiple events to be carried on 'or' operation
     */
    private List<EventType> orEvents;
    /**
     * multiple events to be carried on 'not' operation
     */
    private List<EventType> notEvents;

    Boolean andResult;
    Boolean orResult;
    Boolean notResult;
    Boolean result;

    long interval = 0;
    int runCount;

    public EventCollection() {
        andResult = null;
        orResult = null;
        notResult = null;
        result = TRUE;
    }

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

    }

    @Override
    public String getFieldName() {
        return null;
    }


    @Override
    public void setComparator(String comparator) {

    }

    @Override
    public String getComparator() {
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
    public void setMaxNumberOfRecurrences(Integer recurrence) {

    }

    @Override
    public Integer getMaxNumberOfRecurrences() {
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
    public void and(List<EventType> andEvents) {
        this.andEvents = andEvents;
    }

    @Override
    public List<EventType> getAndEvents() {
        return this.andEvents;
    }

    @Override
    public void or(List<EventType> orEvents) {
        this.orEvents = orEvents;
    }

    @Override
    public List<EventType> getOrEvents() {
        return this.orEvents;
    }

    @Override
    public void not(List<EventType> notEvents) {
        this.notEvents = notEvents;
    }

    @Override
    public List<EventType> getNotEvents() {
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
    public void handle(Context context, EventCallback eventCallback) {
        this.setEventType(EventType.Aggregative_Event);

         /*
         * Deal with logic 'and' operation for multiple events
         * A thread is run to monitor the specified event, and once it is triggered,
         * the boolean variable satisfyCond in each event is set to be true. Callbacks
         * are made use of to monitor the variable change, so that we could know that the
         * event has been triggered.
         */
        if (andEvents != null) {
            for (EventType e : andEvents) {
                // To get the longest interval for all events
                if ((e.getDuration()+e.getInterval()) > interval) {
                    interval = e.getDuration()+e.getInterval();
                }

                final EventType event = e;
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
                e.handle(context, eventCallback);
            }
        }

        /*
         * Deal with logic 'or' operation for multiple events, the same as 'and' operation
         */
        if (orEvents != null) {
            for (EventType e : orEvents) {
                if (e.getInterval() > interval) {
                    interval = e.getInterval();
                }

                final EventType event = e;
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

                e.handle(context, eventCallback);
            }
        }

        /*
         * Deal with logic 'not' operation for multiple events, the same as 'and' operation
         */
        if (notEvents != null) {
            for (EventType e : notEvents) {
                if (e.getInterval() > interval) {
                    interval = e.getInterval();
                }

                final EventType event = e;
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

                e.handle(context, eventCallback);
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
    public static class EventCollectionBuilder {
        private String eventDescription;
        private List<EventType> andEvents = new ArrayList<>();
        private List<EventType> orEvents = new ArrayList<>();
        private List<EventType> notEvents = new ArrayList<>();

        public EventCollectionBuilder setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public EventCollectionBuilder and(EventType andEvent) {
            andEvents.add(andEvent);
            return this;
        }

        public EventCollectionBuilder or(EventType orEvent) {
            orEvents.add(orEvent);
            return this;
        }

        public EventCollectionBuilder not(EventType notEvent) {
            notEvents.add(notEvent);
            return this;
        }

        public EventType build() {
            EventCollection eventCollection = new EventCollection();

            if (andEvents.size() != 0) {
                eventCollection.and(andEvents);
            }

            if (orEvents.size() != 0) {
                eventCollection.or(orEvents);
            }

            if (notEvents.size() != 0) {
                eventCollection.or(notEvents);
            }

            return eventCollection;
        }
    }

}
