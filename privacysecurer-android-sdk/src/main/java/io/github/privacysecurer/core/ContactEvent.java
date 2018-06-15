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

/**
 * Contact related events, used for setting event parameters and providing processing methods.
 */
public class ContactEvent extends Event {

    // Field name options
    public static final String Calls = "calls";
    public static final String Caller = "caller";
    public static final String Emails = "emails";
    public static final String Contacts = "contacts";
    public static final String Logs = "logs";

    // Operator options
    public static final String In = "in";
    public static final String From = "from";
    public static final String Updated = "updated";

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
    private String operator;
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
        this.recurrence = recurrence;
    }

    @Override
    public Integer getNotificationResponsiveness() {
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
    public void addOptimizationConstraints(List<List> batteryIntervalMatrix) {

    }

    @Override
    public void handle(Context context, final PSCallback psCallback) {
        UQI uqi = new UQI(context);
        Boolean booleanFlag = null;
        List<Boolean> list = new ArrayList<>();
        this.context = context;

        // Judge event type
        switch (fieldName) {
            case Calls:
                this.setEventType(Event.Call_Coming_In);
                break;
            case Caller:
                if (operator == From)
                    this.setEventType(Event.Call_Check_Unwanted);
                if (operator == In)
                    this.setEventType(Event.Call_In_List);
                break;
            case Emails:
                this.setEventType(Event.Contact_Emails_In_Lists);
                break;
            case Contacts:
                this.setEventType(Event.Contact_Lists_Updated);
                break;
            case Logs:
                this.setEventType(Event.Call_Logs_Checking);
            default:
                Log.d("Log", "No matchable event type, please check it again.");
        }

        switch (eventType) {
            case Event.Contact_Lists_Updated:
                periodicEvent = true;
                context.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI,true, contactsObserver);
                break;

            case Event.Contact_Emails_In_Lists:
                periodicEvent = false;
                boolean contactFlag = false;

                try {
                    List<List<String>> contactEmails = uqi.getData(Contact.getAll(), Purpose.UTILITY("Listen to contact emails in an existing list."))
                            .asList(Contact.EMAILS);

                    if (contactEmails.size() != 0) {
                        for (int i = 0; i < contactEmails.size(); i++) {
                            for (int j = 0; j < contactEmails.get(i).size(); j++) {
                                if (lists == null) {
                                    Log.d("Log", "Please provide email lists, it's null now.");
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
                            Log.d("Log", "Contact emails in the existing list.");
                            psCallback.setEmails(matchEmails);
                            setSatisfyCond();
                        } else {
                            Log.d("Log", "Event hasn't happened yet.");
                        }
                    } else {
                        Log.d("Log", "No emails in contact lists, please check it.");
                    }
                } catch (PSException e) {
                    e.printStackTrace();
                }
                break;

            case Event.Call_Check_Unwanted:
                periodicEvent = true;

                final PStreamProvider pStreamProvider = Call.asUpdates();
                uqi.getData(pStreamProvider, Purpose.UTILITY("Listen to unwanted incoming calls"))
                        .filter(Call.CONTACT, caller)
                        .ifPresent(Call.CONTACT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != Event.ContinuousSampling && counter > recurrence) {
                                    pStreamProvider.isCancelled = true;
                                } else {
                                    Log.d("Log", "Unwanted incoming calls.");
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            case Event.Call_Logs_Checking:
                periodicEvent = false;
                boolean callFlag = false;

                List<Item> items;
                List<Item> resultItems = new ArrayList<>();
                try {
                    items = uqi.getData(Call.getLogs(), Purpose.UTILITY("Listen to call log event."))
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
                    Log.d("Log", "Unwanted records from call logs.");
                    psCallback.setCallRecords(resultItems);
                    setSatisfyCond();
                } else
                    Log.d("Log", "Event hasn't happened yet.");
                break;

            case Event.Call_In_List:
                periodicEvent = true;

                final PStreamProvider pStreamProvider1 = Call.asUpdates();
                uqi.getData(pStreamProvider1, Purpose.UTILITY("Listen to unwanted incoming calls"))
                        .filterList(Call.CONTACT, lists)
                        .ifPresent(Call.CONTACT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != Event.ContinuousSampling && counter > recurrence) {
                                    pStreamProvider1.isCancelled = true;
                                } else {
                                    Log.d("Log", "Incoming call in the blacklist.");
                                    psCallback.setCaller(input);
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            // A concrete example for Event.Call_In_List
            /*case Event.Call_From_Contacts:
                periodicEvent = true;

                UQI uqiCall = new UQI(context);
                List<String> oneList = new ArrayList<>();
                final PStreamProvider pStreamProvider2 = Call.asUpdates();

                try {
                    List<List<String>> contactPhones = uqi.getData(Contact.getAll(), Purpose.UTILITY("Listen to incoming contact calls"))
                            .asList(Contact.PHONES);
                    for (int i=0; i<contactPhones.size(); i++) {
                        for (int j=0; j<contactPhones.get(i).size(); j++) {
                            //Log.d("Log", contactPhones.get(i).get(j));
                            oneList.add(contactPhones.get(i).get(j));
                        }
                    }

                    uqiCall.getData(pStreamProvider2, Purpose.UTILITY("Listen to incoming contact calls"))
                            .filterList(Call.CONTACT, oneList)
                            .ifPresent(Call.CONTACT, new Callback<String>() {
                                @Override
                                protected void onInput(String input) {

                                    counter++;
                                    satisfyCond = false;
                                    if (recurrence != Event.ContinuousSampling && counter > recurrence) {
                                        pStreamProvider2.isCancelled = true;
                                    } else {
                                        Log.d("Log", "Incoming call from contact lists.");
                                        psCallback.setCaller(input);
                                        setSatisfyCond();
                                    }

                                }
                            });
                } catch (PSException e) {
                    e.printStackTrace();
                }
                break;*/

            case Event.Call_Coming_In:
                periodicEvent = true;

                final PStreamProvider pStreamProvider3 = Call.asUpdates();
                uqi.getData(pStreamProvider3, Purpose.UTILITY("Listen to new calls."))
                        .ifPresent(Call.CONTACT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != Event.ContinuousSampling && counter > recurrence) {
                                    pStreamProvider3.isCancelled = true;
                                } else {
                                    Log.d("Log", "New calls arrive.");
                                    psCallback.setCaller(input);
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            default:
                Log.d("Log", "No contact event matches your input, please check it.");
        }
    }

    /**
     * Builder pattern used to construct contact related events.
     */
    public static class ContactEventBuilder {
        private String fieldName;
        private String operator;
        private List<String> lists;
        private String caller;
        private Integer recurrence;

        public ContactEventBuilder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public ContactEventBuilder setOperator(String operator) {
            this.operator = operator;
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

        public ContactEventBuilder setNotificationResponsiveness(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public Event build() {
            ContactEvent contactEvent = new ContactEvent();
            if (fieldName != null) {
                contactEvent.setFieldName(fieldName);
            }

            if (operator != null) {
                contactEvent.setOperator(operator);
            }

            if (lists != null) {
                contactEvent.setLists(lists);
            }

            if (caller != null) {
                contactEvent.setCaller(caller);
            }

            if (recurrence != null) {
                contactEvent.setNotificationResponsiveness(recurrence);
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
            if (recurrence != Event.ContinuousSampling && counter > recurrence) {
                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(contactsObserver);
            } else {
                Log.d("Log","Contact lists are changed.");
                setSatisfyCond();
            }
        }
    }

}
