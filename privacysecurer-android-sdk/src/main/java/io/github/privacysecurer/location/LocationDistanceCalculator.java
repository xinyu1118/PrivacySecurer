package io.github.privacysecurer.location;


import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;
import io.github.privacysecurer.utils.LocationUtils;

/**
 * Compute the distance between two location fields, in meters.
 */
class LocationDistanceCalculator extends ItemOperator<Double> {

    private final String latLonField1;
    private final String latLonField2;

    LocationDistanceCalculator(String latLonField1, String latLonField2) {
        this.latLonField1 = Assertions.notNull("latLonField1", latLonField1);
        this.latLonField2 = Assertions.notNull("latLonField2", latLonField2);
        this.addParameters(latLonField1, latLonField2);
    }

    @Override
    public Double apply(UQI uqi, Item input) {
        LatLon latLon1 = input.getValueByField(this.latLonField1);
        LatLon latLon2 = input.getValueByField(this.latLonField2);
        return LocationUtils.getDistanceBetween(latLon1, latLon2);
    }
}
