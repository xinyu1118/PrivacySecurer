package io.github.privacysecurer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.github.privacysecurer.core.AudioCallback;
import io.github.privacysecurer.core.AudioEvent;
import io.github.privacysecurer.core.Event;
import io.github.privacysecurer.core.UQI;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UQI uqi = new UQI(this);
        Event audioEvent = new AudioEvent.AudioEventBuilder()
                .setFieldName(AudioEvent.AvgLoudness)
                .setOperator(AudioEvent.GTE)
                .setThreshold(20.0)
                .setDuration(1000)
                .build();
        uqi.addEventListener(audioEvent, new AudioCallback() {
            @Override
            public void onEvent() {
                Log.d("Log", String.valueOf(this.getAvgLoudness()));
            }
        });
    }
}
