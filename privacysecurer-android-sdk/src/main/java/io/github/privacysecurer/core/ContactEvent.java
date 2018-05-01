package io.github.privacysecurer.core;


import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.privacysecurer.communication.Call;
import io.github.privacysecurer.communication.CallOperators;
import io.github.privacysecurer.communication.Contact;
import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;

/**
 * Contact related events, used for setting parameters and providing event processing methods.
 */
public class ContactEvent extends Event {
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
     * The occurrence times for periodic events, e.g. for the Audio_Check_Average_Loudness_Periodically event,
     * setRecurrence(2) means that if the average loudness is higher than the threshold twice, the programming model
     * will stop monitoring the event.
     */
    private Integer recurrence;
    /**
     * The caller name or phone number.
     */
    private String caller;
    /**
     * A list of emails provided by developers.
     */
    private List<String> lists;

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
    public void handle(Context context, final PSCallback psCallback) {
        UQI uqi = new UQI(context);
        Boolean booleanFlag = null;
        List<Boolean> list = new ArrayList<>();
        this.context = context;

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
                    for (int i=0; i<contactEmails.size(); i++) {
                        for (int j=0; j<contactEmails.get(i).size(); j++) {
                            if (lists == null) {
                                Log.d("Log", "Please provide email lists, it's null now.");
                            }
                            for (String email: lists) {
                                if (contactEmails.get(i).get(j).equals(email)) {
                                    contactFlag = true;
                                    matchEmails.add(contactEmails.get(i).get(j));
                                }
                            }
                            //Log.d("Log", contactEmails.get(i).get(j));
                        }
                    }
                    if (contactFlag) {
                        Log.d("Log", "There are contact emails in an existing list.");
                        psCallback.setEmails(matchEmails);
                        setSatisfyCond();
                    } else {
                        Log.d("Log", "There aren't contact emails in an existing list.");
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
                                if (recurrence != null && counter > recurrence) {
                                    //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                    pStreamProvider.isCancelled = true;
                                } else {
                                    Log.d("Log", "Unwanted incoming calls.");
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            case Event.Call_Unwanted_Call_Logs:
                periodicEvent = false;

                try {
                    list = uqi.getData(Call.getLogs(), Purpose.UTILITY("Listen to contact call event."))
                            .setField("callFlag", CallOperators.unwantedCall(Call.CONTACT, caller))
                            .asList("callFlag");
                } catch (PSException e) {
                    e.printStackTrace();
                }
                Boolean tempBoolean = list.get(0);
                for (Boolean l:list) {
                    booleanFlag = Boolean.valueOf(l.booleanValue() || tempBoolean.booleanValue());
                }
                if (booleanFlag != null) {
                    if (booleanFlag) {
                        Log.d("Log", "There are unwanted calls from logs.");
                        setSatisfyCond();
                    }
                    else
                        Log.d("Log", "There aren't unwanted calls from logs.");
                } else {
                    Log.d("Log", "There are no call logs.");
                }
                break;

            case Event.Call_In_Blacklist:
                periodicEvent = true;

                final PStreamProvider pStreamProvider1 = Call.asUpdates();
                uqi.getData(pStreamProvider1, Purpose.UTILITY("Listen to unwanted incoming calls"))
                        .filterList(Call.CONTACT, lists)
                        .ifPresent(Call.CONTACT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != null && counter > recurrence) {
                                    //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                    pStreamProvider1.isCancelled = true;
                                } else {
                                    Log.d("Log", "The incoming phone is in the blacklist.");
                                    psCallback.setCaller(input);
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            case Event.Call_From_Contacts:
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
                                    if (recurrence != null && counter > recurrence) {
                                        //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                        pStreamProvider2.isCancelled = true;
                                    } else {
                                        Log.d("Log", "The incoming phone is from contact lists.");
                                        psCallback.setCaller(input);
                                        setSatisfyCond();
                                    }

                                }
                            });
                } catch (PSException e) {
                    e.printStackTrace();
                }
                break;

            case Event.Call_Coming_In:
                periodicEvent = true;

                final PStreamProvider pStreamProvider3 = Call.asUpdates();
                uqi.getData(pStreamProvider3, Purpose.UTILITY("Listen to new calls."))
                        .ifPresent(Call.CONTACT, new Callback<String>() {
                            @Override
                            protected void onInput(String input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != null && counter > recurrence) {
                                    //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
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
     * Inner class used to build contact related events and corresponding parameters.
     */
    public static class ContactEventBuilder {
        private String eventType;
        private List<String> lists;
        private String caller;
        private Integer recurrence;

        public ContactEventBuilder setEventType(String eventType) {
            this.eventType = eventType;
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

        public ContactEventBuilder setRecurrence(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public Event build() {
            ContactEvent contactEvent = new ContactEvent();
            if (eventType != null) {
                contactEvent.setEventType(eventType);
            }

            if (lists != null) {
                contactEvent.setLists(lists);
            }

            if (caller != null) {
                contactEvent.setCaller(caller);
            }

            if (recurrence != null) {
                contactEvent.setRecurrence(recurrence);
            }

            return contactEvent;
        }
    }

    /**
     * Inner class extends from ContentObserver, and overrides onChange() method,
     * used to monitoring contact lists changes.
     */
    private final class ContactsObserver extends ContentObserver {
        public ContactsObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != null && counter > recurrence) {
                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(contactsObserver);
            } else {
                Log.d("Log","Contact lists are changed.");
                setSatisfyCond();
            }
        }
    }
}