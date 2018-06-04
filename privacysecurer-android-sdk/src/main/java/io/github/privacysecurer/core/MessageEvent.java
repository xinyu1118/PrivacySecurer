package io.github.privacysecurer.core;


import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.privacysecurer.communication.Contact;
import io.github.privacysecurer.communication.Message;
import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;

/**
 * Message related events, used for setting event parameters and providing processing methods.
 */
public class MessageEvent extends Event {
    /**
     * The boolean flag used to indicate whether the event is triggered or not,
     * the initial value is false.
     */
    public boolean satisfyCond = false;
    private BroadListener broadListener;

    /**
     * Event type, defined in Event class.
     */
    private String eventType;
    /**
     * The occurrence times for periodic events, e.g. in the event to monitor unwanted messages,
     * setRecurrence(2) means that if the unwanted caller sends messages twice, the API will
     * stop monitoring the event.
     */
    private Integer recurrence;
    /**
     * The sender name or phone number.
     */
    private String caller;
    /**
     * The blacklist of phone numbers.
     */
    private List<String> lists;

    // used to count the event occurrence times
    int counter = 0;

    private Context context;
    MessagesObserver messagesObserver = new MessagesObserver(new Handler());

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
        this.lists = lists;
    }

    @Override
    public List<String> getLists() {
        return this.lists;
    }

    @Override
    public void setCaller(String caller) {
        this.caller = caller;
    }

    @Override
    public String getCaller() {
        return this.caller;
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
                broadListener.onFail("Receive fail response.");
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

    }

    @Override
    public void addPrecisionConstraints(String lobatPrecision) {

    }


    @Override
    public void handle(Context context, final PSCallback psCallback) {
        UQI uqi = new UQI(context);
        Boolean booleanFlag = null;
        List<Boolean> list = new ArrayList<>();
        this.context = context;

        switch (eventType) {
            case Event.Message_Check_Unwanted:
                periodicEvent = true;

                final PStreamProvider pStreamProvider = Message.asIncomingSMS();
                uqi.getData(pStreamProvider, Purpose.UTILITY("Listen to incoming unwanted messages."))
                        .filter(Message.TYPE, Message.TYPE_RECEIVED)
                        .filter(Message.CONTACT, caller)
                        .ifPresent(Message.CONTENT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {
                                counter++;
                                satisfyCond = false;
                                if (recurrence != null && counter > recurrence) {
                                    pStreamProvider.isCancelled = true;
                                } else {
                                    Log.d("Log", "Unwanted incoming messages.");
                                    setSatisfyCond();
                                }
                            }
                        });
                break;

            case Event.Message_Lists_Updated:
                periodicEvent = true;
                context.getContentResolver().registerContentObserver(Uri.parse("content://sms"),true, messagesObserver);
                break;

            case Event.Message_In_Blacklist:
                periodicEvent = true;

                final PStreamProvider pStreamProvider1 = Message.asIncomingSMS();
                uqi.getData(pStreamProvider1, Purpose.UTILITY("Listen to incoming messages from the blacklist."))
                        .filterList(Message.CONTACT, lists)
                        .ifPresent(Message.CONTACT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != null && counter > recurrence) {
                                    //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                    pStreamProvider1.isCancelled = true;
                                } else {
                                    Log.d("Log", "Incoming message in the blacklist.");
                                    psCallback.setCaller(input);
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            case Event.Message_From_Contacts:
                periodicEvent = true;

                UQI uqiCall = new UQI(context);
                List<String> oneList = new ArrayList<>();

                try {
                    List<List<String>> contactPhones = uqi.getData(Contact.getAll(), Purpose.UTILITY("Listen to incoming messages from contacts."))
                            .asList(Contact.PHONES);

                    for (int i=0; i<contactPhones.size(); i++) {
                        for (int j=0; j<contactPhones.get(i).size(); j++) {
                            oneList.add(contactPhones.get(i).get(j));
                        }
                    }

                    final PStreamProvider pStreamProvider2 = Message.asIncomingSMS();
                    uqiCall.getData(pStreamProvider2, Purpose.UTILITY("Listen to incoming messages from contacts."))
                            .filterList(Message.CONTACT, oneList)
                            .ifPresent(Message.CONTACT, new Callback<String>() {
                                @Override
                                protected void onInput(String input) {
                                    counter++;
                                    satisfyCond = false;
                                    if (recurrence != null && counter > recurrence) {
                                        pStreamProvider2.isCancelled = true;
                                    } else {
                                        Log.d("Log", "Incoming message from contact lists.");
                                        psCallback.setCaller(input);
                                        setSatisfyCond();
                                    }
                                }
                            });

                } catch (PSException e) {
                    e.printStackTrace();
                }
                break;

            case Event.Message_Coming_In:
                periodicEvent = true;

                final PStreamProvider pStreamProvider3 = Message.asIncomingSMS();
                uqi.getData(pStreamProvider3, Purpose.UTILITY("Listen to new messages."))
                        .ifPresent(Message.CONTACT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {
                                counter++;
                                satisfyCond = false;
                                if (recurrence != null && counter > recurrence) {
                                    //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                    pStreamProvider3.isCancelled = true;
                                } else {
                                    Log.d("Log", "New messages arrive.");
                                    psCallback.setCaller(input);
                                    setSatisfyCond();
                                }
                            }
                        });
                break;

            default:
                Log.d("Log", "No message event matches your input, please check it.");
        }

    }

    /**
     * Inner class used to build message related events and corresponding parameters.
     */
    public static class MessageEventBuilder {
        private String eventType;
        private String caller;
        private Integer recurrence;
        private List<String> lists;

        public MessageEventBuilder setEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public MessageEventBuilder setCaller(String caller) {
            this.caller = caller;
            return this;
        }

        public MessageEventBuilder setRecurrence(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public MessageEventBuilder setLists(List<String> lists) {
            this.lists = lists;
            return this;
        }

        public Event build() {
            MessageEvent messageEvent = new MessageEvent();

            if (eventType != null) {
                messageEvent.setEventType(eventType);
            }

            if (caller != null) {
                messageEvent.setCaller(caller);
            }

            if (recurrence != null) {
                messageEvent.setRecurrence(recurrence);
            }

            if (lists != null) {
                messageEvent.setLists(lists);
            }

            return messageEvent;
        }
    }

    /**
     * Inner class extends from ContentObserver, and overrides onChange() method,
     * used to monitoring message changes.
     */
    private final class MessagesObserver extends ContentObserver {
        public MessagesObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != null && counter > recurrence) {
                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(messagesObserver);
            } else {
                Log.d("Log","Messages are changed.");
                setSatisfyCond();
            }
        }
    }
}
