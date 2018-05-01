package io.github.privacysecurer.sensor;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamProvider;
import io.github.privacysecurer.utils.annotations.PSItem;
import io.github.privacysecurer.utils.annotations.PSItemField;

/**
 * Air pressure environment sensor.
 */
@PSItem
public class AirPressure extends Item {

    /**
     * Ambient air pressure. Unit: hPa or mbar.
     */
    @PSItemField(type = Float.class)
    public static final String PRESSURE = "pressure";

    AirPressure(float pressure) {
        this.setFieldValue(PRESSURE, pressure);
    }

    /**
     * Provide a live stream of sensor readings from air pressure sensor.
     * @return the provider.
     */
    public static PStreamProvider asUpdates(int sensorDelay){
        return new AirPressureUpdatesProvider(sensorDelay);
    }
}
