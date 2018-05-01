package io.github.privacysecurer.sensor;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamProvider;
import io.github.privacysecurer.utils.annotations.PSItem;
import io.github.privacysecurer.utils.annotations.PSItemField;

/**
 * Gyroscope.
 */
@PSItem
public class Gyroscope extends Item {

    /**
     * Rate of rotation around the x axis.
     */
    @PSItemField(type = Float.class)
    public static final String X = "x";

    /**
     * Rate of rotation around the y axis.
     */
    @PSItemField(type = Float.class)
    public static final String Y = "y";

    /**
     * Rate of rotation around the z axis.
     */
    @PSItemField(type = Float.class)
    public static final String Z = "z";

    Gyroscope(float x, float y, float z) {
        this.setFieldValue(X, x);
        this.setFieldValue(Y, y);
        this.setFieldValue(Z, z);
    }

    /**
     * Provide a live stream of sensor readings from gyroscope.
     * @return the provider.
     */
    public static PStreamProvider asUpdates(int sensorDelay){
        return new GyroscopeUpdatesProvider(sensorDelay);
    }
}
