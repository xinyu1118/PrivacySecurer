package io.github.privacysecurer.location;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;
import io.github.privacysecurer.utils.LocationUtils;

/**
 * Calculate the distance between current coordinate and destination
 */
public class LocationDestinationCalculator extends ItemOperator<Double> {
    private final String latLonField;
    private final LatLon destination;

    LocationDestinationCalculator(String latLonField, LatLon destination) {
        this.latLonField = Assertions.notNull("latLonField", latLonField);
        this.destination = Assertions.notNull("destination", destination);
        this.addParameters(latLonField, destination);
    }

    @Override
    public Double apply(UQI uqi, Item input) {
        LatLon latLon = input.getValueByField(this.latLonField);
        return LocationUtils.getDistanceBetween(latLon, destination);
    }
}