package io.github.privacysecurer;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.privacysecurer.audio.Audio;
import io.github.privacysecurer.audio.AudioOperators;
import io.github.privacysecurer.communication.Call;

import io.github.privacysecurer.core.AudioCallback;
import io.github.privacysecurer.core.AudioCallbackData;
import io.github.privacysecurer.core.AudioEvent;

import io.github.privacysecurer.core.ContactCallback;
import io.github.privacysecurer.core.ContactCallbackData;
import io.github.privacysecurer.core.ContactEvent;
import io.github.privacysecurer.core.EventCollection;
import io.github.privacysecurer.core.EventCollectionCallback;
import io.github.privacysecurer.core.EventType;

import io.github.privacysecurer.core.GeolocationCallback;
import io.github.privacysecurer.core.GeolocationCallbackData;
import io.github.privacysecurer.core.GeolocationEvent;
import io.github.privacysecurer.core.ImageCallback;
import io.github.privacysecurer.core.ImageCallbackData;
import io.github.privacysecurer.core.ImageEvent;
import io.github.privacysecurer.core.Item;

import io.github.privacysecurer.core.MessageCallback;
import io.github.privacysecurer.core.MessageCallbackData;
import io.github.privacysecurer.core.MessageEvent;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.location.Geolocation;
import io.github.privacysecurer.location.LatLon;

/**
 * Some examples of PrivacyStreams Event for personal data accessing and processing.
 */
public class Examples {
    private UQI uqi;

    public Examples(Context context) {
        this.uqi = new UQI(context);
    }

    public void AvgLoudnessMonitorEvent() {
        // request android.permission.RECORD_AUDIO
        EventType audioEvent = new AudioEvent.AudioEventBuilder()
                .setEventDescription("AvgLoudness")
                .setFieldName(AudioEvent.AvgLoudness)
                .setOperator(AudioEvent.GTE)
                .setThreshold(30.0)
                .setDuration(1000)
                .setInterval(3000)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .addOptimizationConstraints(100, 50, 5000)
                .addOptimizationConstraints(50, 15, 10000)
                .addOptimizationConstraints(15, 0, EventType.Off)
                .build();

        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent(AudioCallbackData audioCallbackData) {
                Log.d("Log", String.valueOf(audioCallbackData.avgLoudness));
                //Log.d("Log", String.valueOf(audioCallbackData.TIME_CREATED));
                //Log.d("Log", String.valueOf(audioCallbackData.EVENT_TYPE));
            }
        });
    }

    public void MaxLoudnessMonitorEvent() {
        // request android.permission.RECORD_AUDIO
        EventType audioEvent = new AudioEvent.AudioEventBuilder()
                                .setFieldName(AudioEvent.MaxLoudness)
                                .setOperator(AudioEvent.GTE)
                                .setThreshold(50.0)
                                .setDuration(1000)
                                .setInterval(3000)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent(AudioCallbackData audioCallbackData) {
                Log.d("Log", String.valueOf(audioCallbackData.maxLoudness));
            }
        });
    }

    public void UserDefinedEvent(){
        EventType audioEvent = new AudioEvent.AudioEventBuilder()
                                .setFieldName("minLoudness", AudioOperators.customizedFunctions(Audio.AUDIO_DATA))
                                .setOperator(AudioEvent.LTE)
                                .setThreshold(30.0)
                                .setDuration(1000)
                                //.setInterval(3000)
                                .build();
        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent(AudioCallbackData audioCallbackData) {
                Log.d("Log", String.valueOf(audioCallbackData.customizedField));
                Log.d("Log", audioCallbackData.EVENT_TYPE);
            }
        });

    }

    public void geoFenceEvent() {
        // to get the local latitude, longitude, speed, bearing etc.
        //uqi.getData(Geolocation.asUpdates(3000, Geolocation.LEVEL_EXACT), Purpose.UTILITY("test"))
        //        .debug();

        // request android.permission.ACCESS_FINE_LOCATION or android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.LatLon)
                                .setOperator(GeolocationEvent.CROSSES)
                                .setLatitude(40.443285)
                                .setLongitude(-79.945502)
                                .setRadius(20.0)
                                .setInterval(3000)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .addOptimizationConstraints(100, 50, 5000, EventType.DefaultPrecision)
                                .addOptimizationConstraints(50, 15, 10000, Geolocation.LEVEL_BUILDING)
                                .addOptimizationConstraints(15, 0, EventType.Off, EventType.DefaultPrecision)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d("Log", String.valueOf(geolocationCallbackData.number));
                //Log.d("Log", String.valueOf(geolocationCallbackData.currentTime));
            }
        });
    }

    public void placeCheckingEvent() {
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.LatLon)
                                .setOperator(GeolocationEvent.IN)
                                .setPlaceName("Newell Simon Hall")
                                .setInterval(3000)
                                .setMaxNumberOfRecurrences(3)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d("Log", String.valueOf(geolocationCallbackData.currentTime));
            }
        });
    }

    public void locationUpdatesEvent() {
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                .setFieldName(GeolocationEvent.LatLon)
                .setOperator(GeolocationEvent.UPDATED)
                .setInterval(3000)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .addOptimizationConstraints(100, 50, 100000, Geolocation.LEVEL_NEIGHBORHOOD)
                .addOptimizationConstraints(50, 15, 60*1000, Geolocation.LEVEL_CITY)
                .addOptimizationConstraints(15, 0, 90*1000, EventType.DefaultPrecision)
                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                LatLon latLon = geolocationCallbackData.latLon;
                Log.d("Log", String.valueOf(latLon.getLatitude()) + ", " + String.valueOf(latLon.getLongitude()));
            }
        });
    }

    public void overspeedEvent() {
        // request android.permission.ACCESS_FINE_LOCATION required or android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                .setFieldName(GeolocationEvent.Speed)
                .setOperator(GeolocationEvent.GTE)
                .setThreshold(0.1)
                .setInterval(3000)
                .setLocationPrecision(Geolocation.LEVEL_EXACT)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d("Log", String.valueOf(geolocationCallbackData.speed));
            }
        });
    }

    public void cityUpdatesEvents() {
        // request android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.City)
                                .setOperator(GeolocationEvent.UPDATED)
                                .setInterval(3000)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d("Log", geolocationCallbackData.city);
            }
        });
    }

    public void postcodeUpdatesEvent() {
        // request android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.Postcode)
                                .setOperator(GeolocationEvent.UPDATED)
                                .setInterval(3000)
                                .setLocationPrecision(Geolocation.LEVEL_BUILDING)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d("Log", geolocationCallbackData.postcode);
            }
        });
    }

    public void distanceCalculatingEvent() {
        // request android.permission.ACCESS_FINE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.Distance)
                                .setOperator(GeolocationEvent.LTE)
                                .setLatitude(40.443277)
                                .setLongitude(-79.945534)
                                .setInterval(3000)
                                .setLocationPrecision(Geolocation.LEVEL_EXACT)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d("Log", String.valueOf(geolocationCallbackData.distance));
            }
        });
    }

    public void directionUpdatesEvent() {
        // request android.permission.ACCESS_FINE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.Direction)
                                .setOperator(GeolocationEvent.UPDATED)
                                .setInterval(3000)
                                .setLocationPrecision(Geolocation.LEVEL_EXACT)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d("Log", geolocationCallbackData.direction);
            }
        });
    }

    public void callBlockerEvent() {
        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS
        EventType callEvent = new ContactEvent.ContactEventBuilder()
                .setFieldName(ContactEvent.Caller)
                .setOperator(ContactEvent.EQ)
                .setCaller("8618515610518")
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                //.setCaller("15555215556")
                .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {

            }
        });
    }

    public void callLogsCheckingEvent() {
        // request android.permission.READ_CALL_LOG
        EventType callEvent = new ContactEvent.ContactEventBuilder()
                            .setFieldName(ContactEvent.Logs)
                            .setOperator(ContactEvent.EQ)
                            .setCaller("8618515610518")
                            .setMaxNumberOfRecurrences(1)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
                List<Item> items = contactCallbackData.callRecords;
                for (Item item : items) {
                    Log.d("Log", "Contact: "+item.getAsString(Call.CONTACT)+", Timestamp: "+String.valueOf(item.getAsLong(Call.TIMESTAMP))
                            +", Duration: "+String.valueOf(item.getAsLong(Call.DURATION))+", Type: "+item.getAsString(Call.TYPE));
                }
            }
        });
    }

    public void callInBlacklistEvent() {
        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS
        List<String> blacklist = new ArrayList<>();
        blacklist.add("8618515610518");
        blacklist.add("14122909962");
        EventType callEvent = new ContactEvent.ContactEventBuilder()
                            .setFieldName(ContactEvent.Caller)
                            .setOperator(ContactEvent.IN)
                            .setLists(blacklist) // runtime
                            .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
                Log.d("Log", contactCallbackData.caller);
            }
        });
    }

    public void callInWhitelistEvent() {
        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS,
        // android.permission.READ_CONTACTS
        /*Event callEvent = new ContactEvent.ContactEventBuilder()
                            .setEventType(UsageEvents.Event.Call_From_Contacts)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/
    }

    public void newCallsEvent() {
        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS
        EventType callEvent = new ContactEvent.ContactEventBuilder()
                            .setFieldName(ContactEvent.Calls)
                            .setOperator(ContactEvent.UPDATED)
                            .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
                Log.d("Log", contactCallbackData.caller);
            }
        });
    }

    public void contactsUpdatesEvent() {
        // request android.permission.READ_CONTACTS
        EventType contactEvent = new ContactEvent.ContactEventBuilder()
                                .setFieldName(ContactEvent.Contacts)
                                .setOperator(ContactEvent.UPDATED)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(contactEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {

            }
        });
    }

    public void emailsSearchingEvent() {
        // request android.permission.READ_CONTACTS
        List<String> emailLists = new ArrayList<>();
        emailLists.add("yangxycl@163.com");
        emailLists.add("test@gmail.com");
        EventType contactEvent = new ContactEvent.ContactEventBuilder()
                                .setFieldName(ContactEvent.Emails)
                                .setOperator(ContactEvent.IN)
                                .setLists(emailLists)
                                .setMaxNumberOfRecurrences(1)
                                .build();
        uqi.addEventListener(contactEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
                List<String> pendingEmails = contactCallbackData.emails;
                if (pendingEmails != null) {
                    for (String email : pendingEmails) {
                        Log.d("Log", email);
                    }
                }
            }
        });
    }

    public void messageBlockerEvent() {
        // request android.permission.RECEIVE_SMS
        EventType messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.Sender)
                                .setOperator(MessageEvent.EQ)
                                .setCaller("8618515610518")
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                //.setCaller("15555215556")
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent(MessageCallbackData messageCallbackData) {

            }
        });
    }

    public void messagesUpdatesEvent() {
        // request android.permission.READ_SMS
        EventType messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.MessageLists)
                                .setOperator(MessageEvent.UPDATED)
                                .setMaxNumberOfRecurrences(1)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent(MessageCallbackData messageCallbackData) {

            }
        });
    }

    public void senderInBlacklistEvent() {
        // request android.permission.RECEIVE_SMS
        List<String> blacklist = new ArrayList<>();
        blacklist.add("8612345678901");
        blacklist.add("15555215556");
        EventType messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.Sender)
                                .setOperator(MessageEvent.IN)
                                .setLists(blacklist)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent(MessageCallbackData messageCallbackData) {
                Log.d("Log", messageCallbackData.caller);
            }
        });
    }

    public void senderInWhitelistEvent() {
        // request android.permission.RECEIVE_SMS and android.permission.READ_CONTACTS
        /*Event messageEvent = new MessageEvent.MessageEventBuilder()
                                .setEventType(Event.Message_From_Contacts)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/
    }

    public void newMessageEvent() {
        // request android.permission.RECEIVE_SMS
        EventType messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.Messages)
                                .setOperator(MessageEvent.UPDATED)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent(MessageCallbackData messageCallbackData) {
                Log.d("Log", messageCallbackData.caller);
            }
        });
    }

    public void mediaUpdatesEvent() {
        // request android.permission.READ_EXTERNAL_STORAGE
        EventType imageEvent = new ImageEvent.ImageEventBuilder()
                            .setFieldName(ImageEvent.MediaLibrary)
                            .setOperator(ImageEvent.UPDATED)
                            .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                            .build();
        uqi.addEventListener(imageEvent, new ImageCallback() {
            @Override
            public void onEvent(ImageCallbackData imageCallbackData) {

            }
        });
    }

    public void facesDetectionEvent() {
        // request android.permission.READ_EXTERNAL_STORAGE
        EventType imageEvent = new ImageEvent.ImageEventBuilder()
                            .setFieldName(ImageEvent.Images)
                            .setFieldName(ImageEvent.HasFace)
                            .setPath("/storage/emulated/0/Download/person.jpg")
                            .setMaxNumberOfRecurrences(1)
                            .build();
        uqi.addEventListener(imageEvent, new ImageCallback() {
            @Override
            public void onEvent(ImageCallbackData imageCallbackData) {

            }
        });
    }

    public void fileUpdatesEvent() {
        // request android.permission.READ_EXTERNAL_STORAGE
        EventType imageEvent = new ImageEvent.ImageEventBuilder()
                            .setFieldName(ImageEvent.FileOrFolder)
                            .setOperator(ImageEvent.UPDATED)
                            .setPath("/storage/emulated/0/DCIM/Camera/")
                            .setMaxNumberOfRecurrences(3)
                            .build();
        uqi.addEventListener(imageEvent, new ImageCallback() {
            @Override
            public void onEvent(ImageCallbackData imageCallbackData) {

            }
        });
    }

    public void eventCollections() {
        EventType audioEvent = new AudioEvent.AudioEventBuilder()
                .setFieldName(AudioEvent.AvgLoudness)
                .setOperator(AudioEvent.GTE)
                .setThreshold(10.0)
                .setDuration(1000)
                .setInterval(3000)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .build();

        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.LatLon)
                                .setOperator(GeolocationEvent.UPDATED)
                                .setInterval(8000)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();

        EventType aggregativeEvent = new EventCollection.EventCollectionBuilder()
                                    .and(audioEvent)
                                    .and(locationEvent)
                                    .build();
        uqi.addEventListener(aggregativeEvent, new EventCollectionCallback() {
            @Override
            public void onEvent(AudioCallbackData audioCallbackData) {

            }

            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {

            }

            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {

            }

            @Override
            public void onEvent(MessageCallbackData messageCallbackData) {

            }

            @Override
            public void onEvent(ImageCallbackData imageCallbackData) {

            }
        });
    }

}
