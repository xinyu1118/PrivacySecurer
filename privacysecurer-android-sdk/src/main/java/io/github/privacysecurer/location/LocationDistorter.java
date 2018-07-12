package io.github.privacysecurer.location;


import io.github.privacysecurer.utils.LocationUtils;

/**
 * Compute the distorted coordinates.
 */
class LocationDistorter extends LocationProcessor<LatLon> {
    private final double radius;

    LocationDistorter(String latLonField, double radius) {
        super(latLonField);
        this.radius = radius;
        this.addParameters(radius);
    }

    @Override
    protected LatLon processLocation(LatLon latLon) {
        return LocationUtils.distortLatLon(latLon, this.radius);
    }
}
