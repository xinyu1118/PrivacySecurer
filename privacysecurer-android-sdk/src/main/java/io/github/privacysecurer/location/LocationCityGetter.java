package io.github.privacysecurer.location;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Get the local city according to current coordinate.
 */
class LocationCityGetter extends CityProcessor<String> {

    LocationCityGetter(String latLonField) {
        super(latLonField);
    }

    @Override
    protected String processCity(Context context, LatLon latLon) {
        double latitude = latLon.getLatitude();
        double longitude = latLon.getLongitude();

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Here 1 represents max location result to be returned, by documents it recommended 1 to 5
        return addresses.get(0).getLocality();
    }

}

