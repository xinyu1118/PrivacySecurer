package io.github.privacysecurer.core;


import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import io.github.privacysecurer.communication.Message;
import io.github.privacysecurer.core.purposes.Purpose;

/**
 * Message related events, used for setting event parameters and providing processing methods.
 */
public class MessageEvent extends EventType {

    // Field name options
    public static final String Messages = "messages";
    public static final String Sender = "sender";
    public static final String MessageLists = "messageLists";

    // Operator options
    public static final String IN = "in";
    public static final String EQ = "eq";
    public static final String UPDATED = "updated";

    /**
     * The boolean flag used to indicate whether the event is triggered or not,
     * the initial value is false.
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
    private String comparator;
    /**
     * The sender name or phone number.
     */
    private String caller;
    /**
     * The blacklist of phone numbers.
     */
    private List<String> lists;
    /**
     * The event recurrence times, could be 0 representing that events happen uninterruptedly,
     * also positive value representing that events happen limited times, especially value 1
     * meaning that events happen only once.
     */
    private Integer recurrence;

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
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    @Override
    public String getComparator() {
        return this.comparator;
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
        this.recurrence = recurrence;
    }

    @Override
    public Integer getMaxNumberOfRecurrences() {
        return this.recurrence;
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
    public void and(List<EventType> andEvents) {

    }

    @Override
    public List<EventType> getAndEvents() {
        return null;
    }

    @Override
    public void or(List<EventType> orEvents) {

    }

    @Override
    public List<EventType> getOrEvents() {
        return null;
    }

    @Override
    public void not(List<EventType> notEvents) {

    }

    @Override
    public List<EventType> getNotEvents() {
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
    public void addOptimizationConstraints(List<List> batteryIntervalMatrix) {

    }


    @Override
    public void handle(Context context, final EventCallback eventCallback) {
        UQI uqi = new UQI(context);
        //Boolean booleanFlag = null;
        //List<Boolean> list = new ArrayList<>();
        this.context = context;
        final MessageCallbackData messageCallbackData = new MessageCallbackData();

        // Judge event type
        switch (fieldName) {
            case Messages:
                this.setEventType(EventType.Message_Coming_In);
                break;
            case Sender:
                if (comparator == EQ)
                    this.setEventType(EventType.Message_Check_Unwanted);
                if (comparator == IN)
                    this.setEventType(EventType.Message_In_List);
                break;
            case MessageLists:
                this.setEventType(EventType.Message_Lists_Updated);
                break;
            default:
                Log.d("Log", "No matchable event type, please check it again.");
        }
        messageCallbackData.setEventType(eventType);

        switch (eventType) {
            case EventType.Message_Check_Unwanted:
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
                                if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                    pStreamProvider.isCancelled = true;
                                } else {
                                    Log.d("Log", "Unwanted incoming messages.");
                                    setSatisfyCond();
                                }
                            }
                        });
                break;

            case EventType.Message_Lists_Updated:
                periodicEvent = true;
                context.getContentResolver().registerContentObserver(Uri.parse("content://sms"),true, messagesObserver);
                break;

            case EventType.Message_In_List:
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
                                    messageCallbackData.setCaller(input);
                                    eventCallback.setMessageCallbackData(messageCallbackData);
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            // A concrete example for Event.Message_In_List
            /*case Event.Message_From_Contacts:
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
                break;*/

            case EventType.Message_Coming_In:
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
                                    messageCallbackData.setCaller(input);
                                    eventCallback.setMessageCallbackData(messageCallbackData);
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
     * Builder pattern used to construct message related events.
     */
    public static class MessageEventBuilder {
        private String eventDescription;
        private String fieldName;
        private String comparator;
        private String caller;
        private List<String> lists;
        private Integer recurrence;

        public MessageEventBuilder setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public MessageEventBuilder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public MessageEventBuilder setComparator(String comparator) {
            this.comparator = comparator;
            return this;
        }

        public MessageEventBuilder setCaller(String caller) {
            this.caller = caller;
            return this;
        }

        public MessageEventBuilder setLists(List<String> lists) {
            this.lists = lists;
            return this;
        }

        public MessageEventBuilder setMaxNumberOfRecurrences(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public EventType build() {
            MessageEvent messageEvent = new MessageEvent();

            if (fieldName != null) {
                messageEvent.setFieldName(fieldName);
            }

            if (comparator != null) {
                messageEvent.setComparator(comparator);
            }

            if (caller != null) {
                messageEvent.setCaller(caller);
            }

            if (lists != null) {
                messageEvent.setLists(lists);
            }

            if (recurrence != null) {
                messageEvent.setMaxNumberOfRecurrences(recurrence);
            }

            return messageEvent;
        }
    }

    /**
     * Inner class extends from ContentObserver, and overrides onChange() method
     * used to monitor message changes.
     */
    private final class MessagesObserver extends ContentObserver {
        public MessagesObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(messagesObserver);
            } else {
                Log.d("Log","Messages are changed.");
                setSatisfyCond();
            }
        }
    }

}
