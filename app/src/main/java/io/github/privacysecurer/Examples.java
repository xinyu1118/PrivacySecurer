package io.github.privacysecurer;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.privacysecurer.audio.Audio;
import io.github.privacysecurer.audio.AudioOperators;

import io.github.privacysecurer.communication.Call;
import io.github.privacysecurer.communication.CallOperators;
import io.github.privacysecurer.communication.Contact;
import io.github.privacysecurer.communication.ContactOperators;
import io.github.privacysecurer.communication.MessageOperators;
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

import io.github.privacysecurer.core.MessageCallback;
import io.github.privacysecurer.core.MessageCallbackData;
import io.github.privacysecurer.core.MessageEvent;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.image.ImageData;
import io.github.privacysecurer.image.ImageOperators;
import io.github.privacysecurer.location.Geolocation;
import io.github.privacysecurer.location.GeolocationOperators;
import io.github.privacysecurer.location.LatLon;
import io.github.privacysecurer.utils.Consts;

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
                .setEventDescription("checking AvgLoudness")
                .setField("avgloudness", AudioOperators.calcAvgLoudness(Audio.AUDIO_DATA))
                .setComparator(AudioEvent.GTE)
                .setThreshold(30.0)
                //.setDuration(1000)
                //.setInterval(3000)
                .setSamplingMode(3000, 1000)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .addOptimizationConstraints(100, 50, 5000, 2000)
                .addOptimizationConstraints(50, 15, 10000)
                .addOptimizationConstraints(15, 0, EventType.Off)
                .build();
        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent(AudioCallbackData audioCallbackData) {
                Log.d(Consts.LIB_TAG, String.valueOf(audioCallbackData.fieldValue));
//                Log.d(Consts.LIB_TAG, String.valueOf(audioCallbackData.TIME_CREATED));
//                Log.d(Consts.LIB_TAG, String.valueOf(audioCallbackData.EVENT_TYPE));
            }
        });
    }

    public void MaxLoudnessMonitorEvent() {
        // request android.permission.RECORD_AUDIO
        EventType audioEvent = new AudioEvent.AudioEventBuilder()
                                .setField("maxLoudness", AudioOperators.calcMaxLoudness(Audio.AUDIO_DATA))
                                .setComparator(AudioEvent.LTE)
                                .setThreshold(50.0)
                                //.setDuration(1000)
                                .setSamplingMode(1000)
                                .setMaxNumberOfRecurrences(1)
                                .build();
        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent(AudioCallbackData audioCallbackData) {
                Log.d(Consts.LIB_TAG, String.valueOf(audioCallbackData.fieldValue));
            }
        });
    }

    public void geoFenceEvent() {
        // to get the local latitude, longitude, speed, bearing etc.
//        uqi.getData(Geolocation.asUpdates(3000, Geolocation.LEVEL_EXACT), Purpose.UTILITY("test"))
//               .debug();

        // request android.permission.ACCESS_FINE_LOCATION or android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setField("location", GeolocationOperators.getLatLon())
                                .setComparator(GeolocationEvent.CROSSES)
                                .setLatitude(40.436839)
                                .setLongitude(-79.951097)
                                .setRadius(20.0)
                                .setSamplingMode(3000)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .addOptimizationConstraints(100, 50, 5000, EventType.DefaultPrecision)
                                .addOptimizationConstraints(50, 15, 10000, Geolocation.LEVEL_BUILDING)
                                .addOptimizationConstraints(15, 0, EventType.Off)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                //Log.d(Consts.LIB_TAG, String.valueOf(geolocationCallbackData.currentTime));
                Log.d(Consts.LIB_TAG, String.valueOf(geolocationCallbackData.number));
            }
        });
    }

    public void placeCheckingEvent() {
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setField("location", GeolocationOperators.getLatLon())
                                .setComparator(GeolocationEvent.IN)
                                .setPlaceName("23 Oakland Square")
                                .setRadius(100.0)
                                .setMaxNumberOfRecurrences(1)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d(Consts.LIB_TAG, String.valueOf(geolocationCallbackData.currentTime));
            }
        });
    }

    public void locationUpdatesEvent() {
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                .setField("location", GeolocationOperators.getLatLon())
                .setComparator(GeolocationEvent.UPDATED)
                .setSamplingMode(3000)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .addOptimizationConstraints(100, 50, 100000, Geolocation.LEVEL_NEIGHBORHOOD)
                .addOptimizationConstraints(50, 15, 60*1000, Geolocation.LEVEL_CITY)
                .addOptimizationConstraints(15, 0, EventType.Off)
                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                LatLon latLon = geolocationCallbackData.latLon;
                Log.d(Consts.LIB_TAG, String.valueOf(latLon.getLatitude()) + ", " + String.valueOf(latLon.getLongitude()));
            }
        });
    }

    public void overspeedEvent() {
        // request android.permission.ACCESS_FINE_LOCATION required or android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                .setField("speed", GeolocationOperators.calcSpeed())
                .setComparator(GeolocationEvent.GTE)
                .setThreshold(0.1)
                .setSamplingMode(3000, Geolocation.LEVEL_EXACT)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d(Consts.LIB_TAG, String.valueOf(geolocationCallbackData.speed));
            }
        });
    }

    public void cityUpdatesEvents() {
        // request android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setField("city", GeolocationOperators.getCity(Geolocation.LAT_LON))
                                .setComparator(GeolocationEvent.UPDATED)
                                .setSamplingMode(3000)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d(Consts.LIB_TAG, geolocationCallbackData.city);
            }
        });
    }

    public void postcodeUpdatesEvent() {
        // request android.permission.ACCESS_COARSE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setField("postcode", GeolocationOperators.getPostcode(Geolocation.LAT_LON))
                                .setComparator(GeolocationEvent.UPDATED)
                                .setSamplingMode(3000, Geolocation.LEVEL_BUILDING)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d(Consts.LIB_TAG, geolocationCallbackData.postcode);
            }
        });
    }

    public void directionUpdatesEvent() {
        // request android.permission.ACCESS_FINE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setField("direction", GeolocationOperators.getDirection())
                                .setComparator(GeolocationEvent.UPDATED)
                                .setSamplingMode(3000, Geolocation.LEVEL_EXACT)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d(Consts.LIB_TAG, geolocationCallbackData.direction);
            }
        });
    }

    public void distanceCalculatingEvent() {
        // request android.permission.ACCESS_FINE_LOCATION
        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setField("distanceTo", GeolocationOperators.distanceTo(Geolocation.LAT_LON) )
                                .setLatitude(40.443277)
                                .setLongitude(-79.945534)
                                .setComparator(GeolocationEvent.LTE)
                                .setThreshold(20.0)
                                .setSamplingMode(3000, Geolocation.LEVEL_EXACT)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent(GeolocationCallbackData geolocationCallbackData) {
                Log.d(Consts.LIB_TAG, String.valueOf(geolocationCallbackData.distance));
            }
        });
    }

    public void callBlockerEvent() {
        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS
        EventType callEvent = new ContactEvent.ContactEventBuilder()
                .setField("caller", CallOperators.callerIdentification())
                .setComparator(ContactEvent.EQ)
                .setPhoneNumber("8618515610518")
                .setMaxNumberOfRecurrences(3)
                .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
            }
        });
    }

//    public void callLogsCheckingEvent() {
//        // request android.permission.READ_CALL_LOG
//        EventType callEvent = new ContactEvent.ContactEventBuilder()
//                            .setFieldName(ContactEvent.Logs)
//                            .setComparator(ContactEvent.EQ)
//                            .setCaller("8618515610518")
//                            .setMaxNumberOfRecurrences(1)
//                            .build();
//        uqi.addEventListener(callEvent, new ContactCallback() {
//            @Override
//            public void onEvent(ContactCallbackData contactCallbackData) {
//                List<Item> items = contactCallbackData.callRecords;
//                for (Item item : items) {
//                    Log.d(Consts.LIB_TAG, "Contact: "+item.getAsString(Call.CONTACT)+", Timestamp: "+String.valueOf(item.getAsLong(Call.TIMESTAMP))
//                            +", Duration: "+String.valueOf(item.getAsLong(Call.DURATION))+", Type: "+item.getAsString(Call.TYPE));
//                }
//            }
//        });
//    }

    public void callInBlacklistEvent() {
        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS
        List<String> blacklist = new ArrayList<>();
        blacklist.add("8618515610518");
        blacklist.add("14122909962");
        blacklist.add("15555215556");
        EventType callEvent = new ContactEvent.ContactEventBuilder()
                            .setField("caller", CallOperators.callerIdentification())
                            .setComparator(ContactEvent.IN)
                            .setContactList(blacklist)
                            .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
                Log.d(Consts.LIB_TAG, contactCallbackData.caller);
            }
        });
    }

//    public void callInWhitelistEvent() {
//        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS,
//        // android.permission.READ_CONTACTS
//        /*Event callEvent = new ContactEvent.ContactEventBuilder()
//                            .setEventType(UsageEvents.Event.Call_From_Contacts)
//                            .build();
//        uqi.addEventListener(callEvent, new ContactCallback() {
//            @Override
//            public void onEvent() {
//                Log.d(Consts.LIB_TAG, this.getCaller());
//            }
//        });*/
//    }

    public void newCallsEvent() {
        // request android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS
        EventType callEvent = new ContactEvent.ContactEventBuilder()
                            .setField("calls", CallOperators.callerIdentification())
                            .setComparator(ContactEvent.UPDATED)
                            .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
                Log.d(Consts.LIB_TAG, contactCallbackData.caller);
            }
        });
    }

    public void contactsUpdatesEvent() {
        // request android.permission.READ_CONTACTS
        EventType contactEvent = new ContactEvent.ContactEventBuilder()
                                .setField("contacts", ContactOperators.getContactLists())
                                .setComparator(ContactEvent.UPDATED)
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
                                .setField("emails", ContactOperators.getContactEmails())
                                .setComparator(ContactEvent.IN)
                                .setContactList(emailLists)
                                .setMaxNumberOfRecurrences(1)
                                .build();
        uqi.addEventListener(contactEvent, new ContactCallback() {
            @Override
            public void onEvent(ContactCallbackData contactCallbackData) {
                List<String> pendingEmails = contactCallbackData.emails;
                if (pendingEmails != null) {
                    for (String email : pendingEmails) {
                        Log.d(Consts.LIB_TAG, email);
                    }
                }
            }
        });
    }

    public void messageBlockerEvent() {
        // request android.permission.RECEIVE_SMS
        // from phone 8618515610518 to 0014122909962
        EventType messageEvent = new MessageEvent.MessageEventBuilder()
                                .setField("sender", MessageOperators.getMessagePhones())
                                .setComparator(MessageEvent.EQ)
                                .setPhoneNumber("8618515610518")
                                //.setCaller("15555215556")
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
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
                                .setField("messages", MessageOperators.getMessageContent())
                                .setComparator(MessageEvent.UPDATED)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
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
        blacklist.add("8618515610518");
        blacklist.add("15555215556");
        EventType messageEvent = new MessageEvent.MessageEventBuilder()
                                .setField("sender", MessageOperators.getMessagePhones())
                                .setComparator(MessageEvent.IN)
                                .setContactList(blacklist)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent(MessageCallbackData messageCallbackData) {
                Log.d(Consts.LIB_TAG, messageCallbackData.caller);
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
                Log.d(Consts.LIB_TAG, this.getCaller());
            }
        });*/
    }

    public void newMessageEvent() {
        // request android.permission.RECEIVE_SMS
        EventType messageEvent = new MessageEvent.MessageEventBuilder()
                                .setField("sender", MessageOperators.getMessagePhones())
                                .setComparator(MessageEvent.UPDATED)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent(MessageCallbackData messageCallbackData) {
                Log.d(Consts.LIB_TAG, messageCallbackData.caller);
            }
        });
    }

    public void mediaUpdatesEvent() {
        // request android.permission.READ_EXTERNAL_STORAGE
        EventType imageEvent = new ImageEvent.ImageEventBuilder()
                            .setField("mediaLibrary", ImageOperators.getImageData())
                            .setComparator(ImageEvent.UPDATED)
                            .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                            .build();
        uqi.addEventListener(imageEvent, new ImageCallback() {
            @Override
            public void onEvent(ImageCallbackData imageCallbackData) {

            }
        });
    }

//    public void facesDetectionEvent() {
//        // request android.permission.READ_EXTERNAL_STORAGE
//        EventType imageEvent = new ImageEvent.ImageEventBuilder()
//                            .setFieldName(ImageEvent.Images)
//                            .setFieldName(ImageEvent.HasFace)
//                            .setPath("/storage/emulated/0/Download/person.jpg")
//                            .setMaxNumberOfRecurrences(1)
//                            .build();
//        uqi.addEventListener(imageEvent, new ImageCallback() {
//            @Override
//            public void onEvent(ImageCallbackData imageCallbackData) {
//
//            }
//        });
//    }

    public void fileUpdatesEvent() {
        // request android.permission.READ_EXTERNAL_STORAGE
        EventType imageEvent = new ImageEvent.ImageEventBuilder()
                            .setField("file", ImageOperators.getImageData())
                            .setComparator(ImageEvent.UPDATED)
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
                .setField("avgLoudness", AudioOperators.calcAvgLoudness(Audio.AUDIO_DATA))
                .setComparator(AudioEvent.GTE)
                .setThreshold(10.0)
                .setSamplingMode(3000, 1000)
                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                .build();

        EventType locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setField("location", GeolocationOperators.getLatLon())
                                .setComparator(GeolocationEvent.UPDATED)
                                .setSamplingMode(8000)
                                .setMaxNumberOfRecurrences(EventType.AlwaysRepeat)
                                .build();

        EventType eventCollections = new EventCollection.EventCollectionBuilder()
                                    .and(audioEvent)
                                    .and(locationEvent)
                                    .build();
        uqi.addEventListener(eventCollections, new EventCollectionCallback() {


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
