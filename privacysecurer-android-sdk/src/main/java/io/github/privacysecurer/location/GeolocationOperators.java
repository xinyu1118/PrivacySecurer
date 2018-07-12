package io.github.privacysecurer.location;


import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

/**
 * A helper class to access geolocation-related operators.
 */
@PSOperatorWrapper
public class GeolocationOperators {
//    /**
//     * Check if the coordinates value of a field is a location at home.
//     *
//     * @param latLonField the coordinates field to check
//     * @return the predicate
//     */
//    public static Function<Item, Boolean> atHome(String latLonField) {
//        return new LocationAtHomePredicate(latLonField);
//    }

    // The LatLon destination parameter will be assigned value when
    // the API begins to deal with geolocation distance calculation event,
    // its initial value is given randomly.
    public static LatLon destination = new LatLon(20.0, -40.0);

    /**
     * Check if the coordinates specified by a LatLon field is a location in an given circular region.
     *
     * @param latLonField the LatLon field to check
     * @param centerLat latitude of the center of the area
     * @param centerLng longitude of the center of the area
     * @param radius radius of the region, in meters
     * @return the function
     */
    public static Function<Item, Boolean> inCircle(String latLonField,
                                                   double centerLat, double centerLng, double radius) {
        return new LocationInCirclePredicate(latLonField, centerLat, centerLng, radius);
    }

    /**
     * Check if the coordinates specified by a LatLon field is a location in an given square region.
     *
     * @param latLonField the LatLon field to check
     * @param minLat the minimum latitude of the region
     * @param minLng the minimum longitude of the region
     * @param maxLat the maximum latitude of the region
     * @param maxLng the maximum longitude of the region
     * @return the function
     */
    public static Function<Item, Boolean> inSquare(String latLonField,
                                                   double minLat, double minLng, double maxLat, double maxLng) {
        return new LocationInSquarePredicate(latLonField, minLat, minLng, maxLat, maxLng);
    }

    /**
     * Distort the coordinates value of a field and return the distorted coordinates.
     * The distorted coordinates is an instance of `LatLon` class.
     *
     * @param latLonField the coordinates field to distort
     * @param radius the distance to distort, in meters
     * @return the function
     */
    public static Function<Item, LatLon> distort(String latLonField, double radius) {
        return new LocationDistorter(latLonField, radius);
    }

    /**
     * Get the distance between two locations (in meters).
     *
     * @param latLonField1 the first location field
     * @param latLonField2 the second location field
     * @return the function
     */
    public static Function<Item, Double> distanceBetween(String latLonField1, String latLonField2) {
        return new LocationDistanceCalculator(latLonField1, latLonField2);
    }

    /**
     * Get the precise location.
     *
     * @return the function
     */
    public static Function<Item, LatLon> getLatLon() {
        String latLonField = Geolocation.LAT_LON;
        return new LocationCoordinateGetter(latLonField);
    }

    /**
     * Get the average speed in m/s.
     *
     * @return the function
     */
    public static Function<Item, Float> calcSpeed() {
        String speedField = Geolocation.SPEED;
        return new LocationSpeedCalculator(speedField);
    }

    /**
     * Calculate the distance between current location and the destination in meters.
     *
     * @param latLonField the coordinates field
     * @return the function
     */
    public static Function<Item, Double> distanceTo(String latLonField) {
        return new LocationDestinationCalculator(latLonField, destination);
    }

    /**
     * Get the current direction using bearing field.
     *
     * @return the function
     */
    public static Function<Item, String> getDirection() {
        String bearingField = Geolocation.BEARING;
        return new LocationDirectionGetter(bearingField);
    }

    /**
     * Get the local postcode based on latitude and longitude.
     *
     * @param latLonField the coordinates field
     * @return the function
     */
    public static Function<Item, String> getPostcode(String latLonField) {
        return new LocationPostcodeGetter(latLonField);
    }

    /**
     * Get the local city based on latitude and longitude.
     *
     * @param latLonField the coordinates field
     * @return the function
     */
    public static Function<Item, String> getCity(String latLonField) {
        return new LocationCityGetter(latLonField);
    }
}
