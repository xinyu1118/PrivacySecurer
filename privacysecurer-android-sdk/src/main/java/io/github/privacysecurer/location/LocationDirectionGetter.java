package io.github.privacysecurer.location;


/**
 * Judge the travel direction using bearing field.
 */
public class LocationDirectionGetter extends BearingProcessor<String> {

    LocationDirectionGetter(String bearingField) {
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