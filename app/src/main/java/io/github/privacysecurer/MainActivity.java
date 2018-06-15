package io.github.privacysecurer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.privacysecurer.communication.Call;
import io.github.privacysecurer.communication.Contact;
import io.github.privacysecurer.core.AggregativeCallback;
import io.github.privacysecurer.core.AggregativeEvent;
import io.github.privacysecurer.core.AudioCallback;
import io.github.privacysecurer.core.AudioEvent;
import io.github.privacysecurer.core.ContactCallback;
import io.github.privacysecurer.core.ContactEvent;
import io.github.privacysecurer.core.Event;
import io.github.privacysecurer.core.GeolocationCallback;
import io.github.privacysecurer.core.GeolocationEvent;
import io.github.privacysecurer.core.ImageCallback;
import io.github.privacysecurer.core.ImageEvent;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.MessageCallback;
import io.github.privacysecurer.core.MessageEvent;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.image.Image;
import io.github.privacysecurer.location.Geolocation;
import io.github.privacysecurer.location.LatLon;
import io.github.privacysecurer.utils.Globals;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Globals.LocationConfig.useGoogleService = true;

        UQI uqi = new UQI(this);

        // check average audio loudness, android.permission.RECORD_AUDIO required
        /*Event audioEvent = new AudioEvent.AudioEventBuilder()
                                .setFieldName(AudioEvent.AvgLoudness)
                                .setOperator(AudioEvent.GTE)
                                .setThreshold(30.0)
                                .setDuration(1000)
                                //.setInterval(3000)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .addOptimizationConstraints(100, 50, 5000)
                                .addOptimizationConstraints(50, 15, 10000)
                                .addOptimizationConstraints(15, 0, Event.Off)
                                .build();
        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", String.valueOf(this.getAvgLoudness()));
            }
        });*/

        // check maximum audio loudness, android.permission.RECORD_AUDIO required
        /*Event audioEvent = new AudioEvent.AudioEventBuilder()
                                .setFieldName(AudioEvent.MaxLoudness)
                                .setOperator(AudioEvent.GTE)
                                .setThreshold(50.0)
                                .setDuration(1000)
                                .setInterval(3000)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", String.valueOf(this.getMaxLoudness()));
            }
        });*/

        /********************************************************************************************/

        // to get the local latitude, longitude, speed, bearing etc.
        //uqi.getData(Geolocation.asUpdates(3000, Geolocation.LEVEL_EXACT), Purpose.UTILITY("test"))
        //        .debug();

        // check location event, android.permission.ACCESS_FINE_LOCATION or
        // android.permission.ACCESS_COARSE_LOCATION required
        Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.LatLon)
                                .setOperator(GeolocationEvent.InOrOut)
                                .setLatitude(40.436838)
                                .setLongitude(-79.951061)
                                .setRadius(20.0)
                                .setInterval(3000)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .addOptimizationConstraints(100, 50, 5000, Event.DefaultPrecision)
                                .addOptimizationConstraints(50, 15, 10000, Geolocation.LEVEL_BUILDING)
                                .addOptimizationConstraints(15, 0, Event.Off, Event.DefaultPrecision)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {

                Log.d("Log", String.valueOf(this.getNumber()));
            }
        });

        /*Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.LatLon)
                                .setOperator(GeolocationEvent.In)
                                .setPlaceName("23 Oakland Square")
                                .setInterval(3000)
                                .setNotificationResponsiveness(3)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", String.valueOf(this.getCurrentTime()));
            }
        });*/

        /*Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.LatLon)
                                .setOperator(GeolocationEvent.Updated)
                                .setInterval(3000)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {
                LatLon latLon = this.getLatLon();
                Log.d("Log", String.valueOf(latLon.getLatitude())+", "+String.valueOf(latLon.getLongitude()));
            }
        });*/

        // check speed event, android.permission.ACCESS_FINE_LOCATION required or
        // android.permission.ACCESS_COARSE_LOCATION required
        /*Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.Speed)
                                .setOperator(GeolocationEvent.Over)
                                .setThreshold(0.1)
                                .setInterval(3000)
                                .setLocationPrecision(Geolocation.LEVEL_EXACT)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", String.valueOf(this.getSpeed()));
            }
        });*/

        // check city event, android.permission.ACCESS_COARSE_LOCATION required
        /*Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.City)
                                .setOperator(GeolocationEvent.Updated)
                                .setInterval(3000)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCity());
            }
        });*/

        // check postcode event, android.permission.ACCESS_COARSE_LOCATION required
        /*Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.Postcode)
                                .setOperator(GeolocationEvent.Updated)
                                .setInterval(3000)
                                .setLocationPrecision(Geolocation.LEVEL_BUILDING)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getPostcode());
            }
        });*/

        // check distance event, android.permission.ACCESS_FINE_LOCATION required
        /*Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.Distance)
                                .setOperator(GeolocationEvent.Over)
                                .setLatitude(40.443277)
                                .setLongitude(-79.945534)
                                .setInterval(3000)
                                .setLocationPrecision(Geolocation.LEVEL_EXACT)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", String.valueOf(this.getDistance()));
            }
        });*/

        // check direction event, android.permission.ACCESS_FINE_LOCATION required
        /*Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.Direction)
                                .setOperator(GeolocationEvent.Updated)
                                .setInterval(3000)
                                .setLocationPrecision(Geolocation.LEVEL_EXACT)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(locationEvent, new GeolocationCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getDirection());
            }
        });*/

        /********************************************************************************************/

        // monitor incoming calls from a certain phone number,
        // android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS required
        /*Event callEvent = new ContactEvent.ContactEventBuilder()
                .setFieldName(ContactEvent.Caller)
                .setOperator(ContactEvent.From)
                .setCaller("8618515610518")
                .setNotificationResponsiveness(Event.ContinuousSampling)
                //.setCaller("15555215556")
                .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent() {

            }
        });*/

        // check whether there are unwanted calls from logs, android.permission.READ_CALL_LOG required
        /*Event callEvent = new ContactEvent.ContactEventBuilder()
                            .setFieldName(ContactEvent.Logs)
                            .setOperator(ContactEvent.From)
                            .setCaller("8618515610518")
                            //.setNotificationResponsiveness(1)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent() {
               List<Item> items = this.getCallRecords();
               for (Item item : items) {
                   Log.d("Log", "Contact: "+item.getAsString(Call.CONTACT)+", Timestamp: "+String.valueOf(item.getAsLong(Call.TIMESTAMP))
                   +", Duration: "+String.valueOf(item.getAsLong(Call.DURATION))+", Type: "+item.getAsString(Call.TYPE));
               }
            }
        });*/

        // check if the incoming call is in a blacklist,
        // android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS required
        /*List<String> blacklist = new ArrayList<>();
        blacklist.add("8618515610518");
        blacklist.add("14122909962");
        Event callEvent = new ContactEvent.ContactEventBuilder()
                            .setFieldName(ContactEvent.Caller)
                            .setOperator(ContactEvent.In)
                            .setLists(blacklist)
                            .setNotificationResponsiveness(Event.ContinuousSampling)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/

        // check if the incoming call is in a contact list,
        // android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS,
        // android.permission.READ_CONTACTS required
        /*Event callEvent = new ContactEvent.ContactEventBuilder()
                            .setEventType(Event.Call_From_Contacts)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/

        // listen to incoming calls,
        // android.permission.READ_PHONE_STATE, android.permission.PROCESS_OUTGOING_CALLS required
        /*Event callEvent = new ContactEvent.ContactEventBuilder()
                            .setFieldName(ContactEvent.Calls)
                            .setOperator(ContactEvent.Updated)
                            .setNotificationResponsiveness(Event.ContinuousSampling)
                            .build();
        uqi.addEventListener(callEvent, new ContactCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/

        // check contact list changes, android.permission.READ_CONTACTS required
        /*Event contactEvent = new ContactEvent.ContactEventBuilder()
                                .setFieldName(ContactEvent.Contacts)
                                .setOperator(ContactEvent.Updated)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(contactEvent, new ContactCallback() {
            @Override
            public void onEvent() {

            }
        });*/

        // check whether there is a contact email in an existing lists, android.permission.READ_CONTACTS required
        /*List<String> emailLists = new ArrayList<>();
        emailLists.add("yangxycl@163.com");
        emailLists.add("test@gmail.com");
        Event contactEvent = new ContactEvent.ContactEventBuilder()
                                .setFieldName(ContactEvent.Emails)
                                .setOperator(ContactEvent.In)
                                .setLists(emailLists)
                                .build();
        uqi.addEventListener(contactEvent, new ContactCallback() {
            @Override
            public void onEvent() {
                List<String> pendingEmails = this.getEmails();
                if (pendingEmails != null) {
                    for (String email : pendingEmails) {
                        Log.d("Log", email);
                    }
                }
            }
        });*/

        /********************************************************************************************/

        // monitor incoming messages from a certain caller, android.permission.RECEIVE_SMS required
        /*Event messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.Sender)
                                .setOperator(MessageEvent.From)
                                .setCaller("8618515610518")
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                //.setCaller("15555215556")
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent() {

            }
        });*/

        // monitor message content changes, android.permission.READ_SMS required
        /*Event messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.MessageLists)
                                .setOperator(MessageEvent.Updated)
                                .setNotificationResponsiveness(1)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent() {

            }
        });*/

        // monitor incoming messages from the blacklist, android.permission.RECEIVE_SMS required
        /*List<String> blacklist = new ArrayList<>();
        blacklist.add("8618515610518");
        blacklist.add("15555215556");
        Event messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.Sender)
                                .setOperator(MessageEvent.In)
                                .setLists(blacklist)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/

        // monitor incoming messages from contact lists, android.permission.RECEIVE_SMS and
        // android.permission.READ_CONTACTS required
        /*Event messageEvent = new MessageEvent.MessageEventBuilder()
                                .setEventType(Event.Message_From_Contacts)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/

        // monitor incoming messages, android.permission.RECEIVE_SMS required
        /*Event messageEvent = new MessageEvent.MessageEventBuilder()
                                .setFieldName(MessageEvent.Messages)
                                .setOperator(MessageEvent.Updated)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();
        uqi.addEventListener(messageEvent, new MessageCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", this.getCaller());
            }
        });*/

        /********************************************************************************************/

        // monitor image content changes, android.permission.READ_EXTERNAL_STORAGE required
        // e.g. delete an image in /storage/emulated/0 folder
        // folder and subfolder changes could both be monitored
        /*Event imageEvent = new ImageEvent.ImageEventBuilder()
                            .setFieldName(ImageEvent.MediaLibrary)
                            .setOperator(ImageEvent.Updated)
                            .setNotificationResponsiveness(Event.ContinuousSampling)
                            .build();
        uqi.addEventListener(imageEvent, new ImageCallback() {
            @Override
            public void onEvent() {

            }
        });*/

        // check whether there is a face in an image, android.permission.READ_EXTERNAL_STORAGE required
        /*Event imageEvent = new ImageEvent.ImageEventBuilder()
                            .setFieldName(ImageEvent.Images)
                            .setFieldName(ImageEvent.HasFace)
                            .setPath("/storage/emulated/0/Download/person.jpg")
                            .setNotificationResponsiveness(1)
                            .build();
        uqi.addEventListener(imageEvent, new ImageCallback() {
            @Override
            public void onEvent() {

            }
        });*/

        // check the content changes of a file folder, android.permission.READ_EXTERNAL_STORAGE required
        /*Event imageEvent = new ImageEvent.ImageEventBuilder()
                            .setFieldName(ImageEvent.FileOrFolder)
                            .setOperator(ImageEvent.Updated)
                            .setPath("/storage/emulated/0/DCIM/Camera/")
                            .setNotificationResponsiveness(3)
                            .build();
        uqi.addEventListener(imageEvent, new ImageCallback() {
            @Override
            public void onEvent() {

            }
        });*/

        // check aggragative events
        /*Event audioEvent = new AudioEvent.AudioEventBuilder()
                .setFieldName(AudioEvent.AvgLoudness)
                .setOperator(AudioEvent.GTE)
                .setThreshold(10.0)
                .setDuration(1000)
                .setInterval(3000)
                .setNotificationResponsiveness(Event.ContinuousSampling)
                .build();

        Event locationEvent = new GeolocationEvent.GeolocationEventBuilder()
                                .setFieldName(GeolocationEvent.LatLon)
                                .setOperator(GeolocationEvent.Updated)
                                .setInterval(8000)
                                .setNotificationResponsiveness(Event.ContinuousSampling)
                                .build();

        Event aggregativeEvent = new AggregativeEvent.AggregativeEventBuilder()
                                    .and(audioEvent)
                                    .and(locationEvent)
                                    .build();
        uqi.addEventListener(aggregativeEvent, new AggregativeCallback() {
            @Override
            public void onEvent() {

            }
        });*/
    }
}
