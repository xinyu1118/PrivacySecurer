package io.github.privacysecurer.location;


import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Get the precise location.
 */
public class LocationCoordinateGetter extends ItemOperator<LatLon>{
    private final String latLonField;

    LocationCoordinateGetter(String latLonField) {
        this.latLonField = Assertions.notNull("latLonField", latLonField);
        this.addParameters(latLonField);
    }

    @Override
    public LatLon apply(UQI uqi, Item input) {
        return input.getValueByField(this.latLonField);
    }
}
