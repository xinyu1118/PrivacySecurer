package io.github.privacysecurer;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.privacysecurer.utils.Globals;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Globals.LocationConfig.useGoogleService = true;

        Examples examples = new Examples(this);
        //examples.AvgLoudnessMonitorEvent();
        //examples.MaxLoudnessMonitorEvent();

        //examples.geoFenceEvent();
        //examples.placeCheckingEvent();
        //examples.locationUpdatesEvent();
        //examples.overspeedEvent();
        //examples.cityUpdatesEvents();
        //examples.postcodeUpdatesEvent();
        //examples.distanceCalculatingEvent();
        //examples.directionUpdatesEvent();

        //examples.callBlockerEvent();
        //examples.callLogsCheckingEvent();
        //examples.callInBlacklistEvent();
        //examples.newCallsEvent();
        //examples.contactsUpdatesEvent();
        //examples.emailsSearchingEvent();

        //examples.messageBlockerEvent();
        //examples.messagesUpdatesEvent();
        //examples.senderInBlacklistEvent();
        //examples.newMessageEvent();

        //examples.mediaUpdatesEvent();
        //examples.facesDetectionEvent();
        //examples.fileUpdatesEvent();

        //examples.eventCollections();
    }
}
