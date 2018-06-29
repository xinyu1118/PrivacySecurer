package io.github.privacysecurer.location;


/**
 * Obtain the current direction using bearing.
 */
public class LocationDirectionPredicate extends BearingProcessor<String> {

    LocationDirectionPredicate(String bearingField) {
        super(bearingField);
    }

    @Override
    protected String processBearing(Float bearing) {
        if (bearing == null) return null;
        if ( bearing >= 0.0 && bearing < 180.0) {
            return "right";
        } else {
            return "left";
        }
    }
}