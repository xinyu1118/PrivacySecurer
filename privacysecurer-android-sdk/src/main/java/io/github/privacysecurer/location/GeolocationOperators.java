package io.github.privacysecurer.location;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

/**
 * A helper class to access geolocation-related operators
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
     * @param latLonField1 the first location
     * @param latLonField2 the second location
     * @return the function
     */
    public static Function<Item, Double> distanceBetween(String latLonField1, String latLonField2) {
        return new LocationDistanceCalculator(latLonField1, latLonField2);
    }

    /**
     * Check the distance between current location and destination in meters.
     * The difference between arriveDestination and distanceBetween method is the parameter type.
     *
     * @param latLonField the field name of current coordinate
     * @param destination the destination
     * @return the function
     */
    public static Function<Item, Double> arriveDestination(String latLonField, LatLon destination) {
        return new LocationDestinationCalculator(latLonField, destination);
    }

    /**
     * Check over speed in m/s.
     *
     * @param speedField the field name of current speed
     * @param threshold the speed limitation
     * @return the function
     */
    public static Function<Item, Boolean> checkSpeed(String speedField, Float threshold) {
        return new LocationSpeedPredicate(speedField, threshold);
    }

    /**
     * Get the current direction using bearing.
     *
     * @param bearingField the horizontal direction of travel of this device
     * @return the function
     */
    public static Function<Item, String> getDirection(String bearingField) {
        return new LocationDirectionPredicate(bearingField);
    }

    /**
     * Get the postcode based on latitude and longitude.
     *
     * @param latLonField the field name of current coordinate
     * @return the function
     */
    public static Function<Item, String> getPostcode (String latLonField) {
        return new LocationPostcodeOperator(latLonField);
    }

    /**
     * Get the city based on latitude and longitude.
     *
     * @param latLonField the field name of current coordinate
     * @return the function
     */
    public static Function<Item, String> getCity (String latLonField) {
        return new LocationCityOperator(latLonField);
    }


//    /**
//     * Get the postcode string based on the coordinates value of a field.
//     *
//     * @param latLonField the coordinates field
//     * @return the function
//     */
//    public static Function<Item, String> asPostcode(String latLonField) {
//        return new LocationPostcodeOperator(latLonField);
//    }
//
//    /**
//     * Get the geotag string based on the coordinates value of a field.
//     *
//     * @param latLonField the coordinates field
//     * @return the function
//     */
//    public static Function<Item, String> asGeotag(String latLonField) {
//        return new LocationGeoTagger(latLonField);
//    }
}
