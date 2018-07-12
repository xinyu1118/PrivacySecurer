package io.github.privacysecurer.core;


import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.privacysecurer.communication.Call;
import io.github.privacysecurer.communication.Contact;
import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.utils.Consts;

import static io.github.privacysecurer.utils.Assertions.notNull;

/**
 * Contact related events, used for setting event parameters and providing processing methods.
 */
public class ContactEvent<TValue> extends EventType {

    // Field name options
//    public static final String Calls = "calls";
//    public static final String Caller = "caller";
//    public static final String Emails = "emails";
//    public static final String Contacts = "contacts";
//    public static final String Logs = "logs";

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
     * The field value calculation function.
     */
    private Function<Item, TValue> fieldCalculationFunction;
    /**
     * The operator on the field value.
     */
    private String comparator;
    /**
     * A list of emails provided by developers.
     */
    private List<String> lists;
    /**
     * The caller name or phone number.
     */
    private String caller;
    /**
     * The event recurrence times, could be 0 representing that events happen uninterruptedly,
     * also positive value representing that events happen limited times, especially value 1
     * meaning that events happen only once.
     */
    private Integer recurrence;

    // used to count the event occurrence times
    int counter = 0;

    // used to store the emails matched with contact email lists
    List<String> matchEmails = new ArrayList<>();

    private Context context;
    ContactsObserver contactsObserver = new ContactsObserver(new Handler());

    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public <T> void setField(String fieldName, Function<Item, T> fieldCalculationFunction) {
        this.fieldName = fieldName;
        this.fieldCalculationFunction = (Function<Item, TValue>) fieldCalculationFunction;
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
        final ContactCallbackData contactCallbackData = new ContactCallbackData();
        contactCallbackData.setEventType(eventType);

        switch (eventType) {
            case EventType.Contact_Lists_Updated:
                periodicEvent = true;
                if (fieldName == null) Log.d(Consts.LIB_TAG, "You haven't set field yet, it couldn't be null.");
                if (comparator == null) Log.d(Consts.LIB_TAG, "You haven't set comparator yet, it couldn't be null.");
                if (recurrence == null) Log.d(Consts.LIB_TAG, "You haven't set recurrence yet, it couldn't be null.");

                context.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI,true, contactsObserver);
                break;

            case EventType.Contact_Emails_In_Lists:
                periodicEvent = false;
                boolean contactFlag = false;

                if (fieldName == null) Log.d(Consts.LIB_TAG, "You haven't set field yet, it couldn't be null.");
                if (comparator == null) Log.d(Consts.LIB_TAG, "You haven't set comparator yet, it couldn't be null.");
                if (lists == null) Log.d(Consts.LIB_TAG, "You haven't set the list yet, it couldn't be null.");

                try {
                    List<List<String>> contactEmails = uqi.getData(Contact.getAll(), Purpose.UTILITY("Monitor contact emails."))
                            .setField(fieldName, fieldCalculationFunction)
                            .asList(fieldName);

                    if (contactEmails.size() != 0) {
                        for (int i = 0; i < contactEmails.size(); i++) {
                            for (int j = 0; j < contactEmails.get(i).size(); j++) {
                                if (lists == null) {
                                    Log.d(Consts.LIB_TAG, "Please provide email lists, it's null now.");
                                }
                                for (String email : lists) {
                                    if (contactEmails.get(i).get(j).equals(email)) {
                                        contactFlag = true;
                                        matchEmails.add(contactEmails.get(i).get(j));
                                    }
                                }
                            }
                        }
                        if (contactFlag) {
                            Log.d(Consts.LIB_TAG, "There are contact emails in a given list.");
                            contactCallbackData.setEmails(matchEmails);
                            eventCallback.setContactCallbackData(contactCallbackData);
                            setSatisfyCond();
                        } else {
                            Log.d(Consts.LIB_TAG, "Event hasn't happened yet.");
                        }
                    } else {
                        Log.d(Consts.LIB_TAG, "No emails in contact lists, please check it.");
                    }
                } catch (PSException e) {
                    e.printStackTrace();
                }
                break;

            case EventType.Call_Check_Unwanted:
                periodicEvent = true;

                if (fieldName == null) Log.d(Consts.LIB_TAG, "You haven't set field yet, it couldn't be null.");
                if (comparator == null) Log.d(Consts.LIB_TAG, "You haven't set comparator yet, it couldn't be null.");
                if (caller == null) Log.d(Consts.LIB_TAG, "You haven't set caller phone number yet, it couldn't be null.");
                if (recurrence == null) Log.d(Consts.LIB_TAG, "You haven't set recurrence yet, it couldn't be null.");

                final PStreamProvider pStreamProvider = Call.asUpdates();
                uqi.getData(pStreamProvider, Purpose.UTILITY("Monitor callers."))
                        .setField(fieldName, fieldCalculationFunction)
                        .forEach(fieldName, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {
                                if (input.equals(caller)) {
                                    counter++;
                                    satisfyCond = false;
                                    if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                        pStreamProvider.isCancelled = true;
                                    } else {
                                        Log.d(Consts.LIB_TAG, "The caller is from a given phone number.");
                                        setSatisfyCond();
                                    }
                                } else {
                                    Log.d(Consts.LIB_TAG, "Event hasn't happened yet.");
                                    satisfyCond = false;
                                }
                            }
                        });
                break;

            case EventType.Call_Logs_Checking:
                periodicEvent = false;
                boolean callFlag = false;

                List<Item> items;
                List<Item> resultItems = new ArrayList<>();
                try {
                    items = uqi.getData(Call.getLogs(), Purpose.UTILITY("Monitor call logs."))
                            .asList();
                    for (Item item : items) {
                        if (item.containsField(Call.CONTACT)) {
                            if (item.getAsString(Call.CONTACT).equals(caller)) {
                                callFlag = true;
                                resultItems.add(item);
                            }
                        }
                    }
                } catch (PSException e) {
                    e.printStackTrace();
                }

                if (callFlag) {
                    Log.d(Consts.LIB_TAG, "There are unwanted records from call logs.");
                    contactCallbackData.setCallRecords(resultItems);
                    eventCallback.setContactCallbackData(contactCallbackData);
                    setSatisfyCond();
                } else
                    Log.d(Consts.LIB_TAG, "Event hasn't happened yet.");
                break;

            case EventType.Call_In_List:
                periodicEvent = true;

                if (fieldName == null) Log.d(Consts.LIB_TAG, "You haven't set field yet, it couldn't be null.");
                if (comparator == null) Log.d(Consts.LIB_TAG, "You haven't set comparator yet, it couldn't be null.");
                if (lists == null) Log.d(Consts.LIB_TAG, "You haven't set the list yet, it couldn't be null.");
                if (recurrence == null) Log.d(Consts.LIB_TAG, "You haven't set recurrence yet, it couldn't be null.");

                final PStreamProvider pStreamProvider1 = Call.asUpdates();
                uqi.getData(pStreamProvider1, Purpose.UTILITY("Monitor callers."))
                        .setField(fieldName, fieldCalculationFunction)
                        .forEach(fieldName, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {
                                for (String list : lists) {
                                    if (input.equals(list)) {
                                        counter++;
                                        satisfyCond = false;
                                        if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                            pStreamProvider1.isCancelled = true;
                                        } else {
                                            Log.d(Consts.LIB_TAG, "The caller is in a given list.");
                                            contactCallbackData.setCaller(input);
                                            eventCallback.setContactCallbackData(contactCallbackData);
                                            setSatisfyCond();
                                        }
                                        break;
                                    }
                                }
                            }
                        });
                break;

            // A concrete example for Event.Call_In_List
//            case Event.Call_From_Contacts:
//                periodicEvent = true;
//                UQI uqiCall = new UQI(context);
//                List<String> oneList = new ArrayList<>();
//                final PStreamProvider pStreamProvider2 = Call.asUpdates();
//
//                try {
//                    List<List<String>> contactPhones = uqi.getData(Contact.getAll(), Purpose.UTILITY("Listen to incoming contact calls"))
//                            .asList(Contact.PHONES);
//                    for (int i=0; i<contactPhones.size(); i++) {
//                        for (int j=0; j<contactPhones.get(i).size(); j++) {
//                            //Log.d(Consts.LIB_TAG, contactPhones.get(i).get(j));
//                            oneList.add(contactPhones.get(i).get(j));
//                        }
//                    }
//
//                    uqiCall.getData(pStreamProvider2, Purpose.UTILITY("Listen to incoming contact calls"))
//                            .filterList(Call.CONTACT, oneList)
//                            .ifPresent(Call.CONTACT, new Callback<String>() {
//                                @Override
//                                protected void onInput(String input) {
//
//                                    counter++;
//                                    satisfyCond = false;
//                                    if (recurrence != Event.ContinuousSampling && counter > recurrence) {
//                                        pStreamProvider2.isCancelled = true;
//                                    } else {
//                                        Log.d(Consts.LIB_TAG, "Incoming call from contact lists.");
//                                        psCallback.setCaller(input);
//                                        setSatisfyCond();
//                                    }
//
//                                }
//                            });
//                } catch (PSException e) {
//                    e.printStackTrace();
//                }
//                break;

            case EventType.Call_Coming_In:
                periodicEvent = true;

                if (fieldName == null) Log.d(Consts.LIB_TAG, "You haven't set field yet, it couldn't be null.");
                if (comparator == null) Log.d(Consts.LIB_TAG, "You haven't set comparator yet, it couldn't be null.");
                if (recurrence == null) Log.d(Consts.LIB_TAG, "You haven't set recurrence yet, it couldn't be null.");

                final PStreamProvider pStreamProvider3 = Call.asUpdates();
                uqi.getData(pStreamProvider3, Purpose.UTILITY("Listen to new calls."))
                        .setField(fieldName, fieldCalculationFunction)
                        .ifPresent(fieldName, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {
                                counter++;
                                satisfyCond = false;
                                if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                    pStreamProvider3.isCancelled = true;
                                } else {
                                    Log.d(Consts.LIB_TAG, "New call arrives.");
                                    contactCallbackData.setCaller(input);
                                    eventCallback.setContactCallbackData(contactCallbackData);
                                    setSatisfyCond();
                                }
                            }
                        });
                break;

            default:
                Log.d(Consts.LIB_TAG, "No contact event matches your input, please check it.");
        }
    }

    /**
     * Builder pattern used to construct contact related events.
     */
    public static class ContactEventBuilder<TValue> {
        private String eventDescription;
        private String fieldName;
        private Function<Item, TValue> fieldCalculationFunction;
        private String functionName;
        private String comparator;
        private List<String> lists;
        private String caller;
        private Integer recurrence;

        public ContactEventBuilder setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public <Tout> ContactEventBuilder setField(String fieldName, Function<Item, Tout> fieldCalculationFunction) {
            this.fieldName = fieldName;
            this.fieldCalculationFunction = (Function<Item, TValue>) notNull("fieldCalculationFunction", fieldCalculationFunction);
            this.functionName = fieldCalculationFunction.getClass().getSimpleName();
            return this;
        }

        public ContactEventBuilder setComparator(String comparator) {
            this.comparator = comparator;
            return this;
        }

        public ContactEventBuilder setLists(List<String> lists) {
            this.lists = lists;
            return this;
        }

        public ContactEventBuilder setCaller(String caller) {
            this.caller = caller;
            return this;
        }

        public ContactEventBuilder setMaxNumberOfRecurrences(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public EventType build() {
            ContactEvent contactEvent = new ContactEvent();
            if (fieldName != null) {
                contactEvent.setField(fieldName, fieldCalculationFunction);
            }

            if (comparator != null) {
                contactEvent.setComparator(comparator);
            }

            if (lists != null) {
                contactEvent.setLists(lists);
            }

            if (caller != null) {
                contactEvent.setCaller(caller);
            }

            if (recurrence != null) {
                contactEvent.setMaxNumberOfRecurrences(recurrence);
            }

            // Judge event type
            switch (functionName) {
                case "CallNumberGetter":
                    if (comparator.equals(EQ)) contactEvent.setEventType(EventType.Call_Check_Unwanted);
                    if (comparator.equals(IN)) contactEvent.setEventType(EventType.Call_In_List);
                    if (comparator.equals(UPDATED)) contactEvent.setEventType(EventType.Call_Coming_In);
                    break;
                case "ContactEmailGetter":
                    contactEvent.setEventType(EventType.Contact_Emails_In_Lists);
                    break;
                case "ContactListsGetter":
                    contactEvent.setEventType(EventType.Contact_Lists_Updated);
                    break;
                default:
                    Log.d(Consts.LIB_TAG, "No matchable event type, please check it again.");
            }

            return contactEvent;
        }
    }

    /**
     * Inner class extends from ContentObserver, and overrides onChange() method
     * used to monitor contact lists changes.
     */
    private final class ContactsObserver extends ContentObserver {
        public ContactsObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                //Log.d(Consts.LIB_TAG, "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(contactsObserver);
            } else {
                Log.d(Consts.LIB_TAG,"Contact lists are updated.");
                setSatisfyCond();
            }
        }
    }

}
