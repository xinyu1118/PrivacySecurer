package io.github.privacysecurer.location;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Check whether the current speed is over the limitation.
 */
public class LocationSpeedPredicate extends SpeedProcessor<Boolean> {
    private Float threshold = null;

    LocationSpeedPredicate(String speedField, Float threshold) {
        super(speedField);
        this.threshold = threshold;
        this.addParameters(threshold);
    }

    @Override
    protected Boolean processSpeed(Float speed) {
        if (speed == null) return null;
        return speed >= threshold? TRUE: FALSE;
    }
}
