package io.github.privacysecurer.location;


/**
 * Calculate average speed in m/s.
 */
public class LocationSpeedCalculator extends SpeedProcessor<Float> {

    LocationSpeedCalculator(String speedField) {
        super(speedField);
    }

    @Override
    protected Float processSpeed(Float speed) {
        if (speed == null) return null;
        return speed;
    }
}