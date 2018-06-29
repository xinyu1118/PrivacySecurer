package io.github.privacysecurer.core;


import io.github.privacysecurer.location.LatLon;

/**
 * Location callback data containing several fine-grained fields.
 */
public class GeolocationCallbackData extends CallbackData {
    /**
     * The local LatLon, including latitude and longitude.
     */
    public LatLon latLon;
    /**
     * The current speed in m/s.
     */
    public Float speed;
    /**
     * The distance to the destination.
     */
    public Double distance;
    /**
     * The current direction, left or right.
     */
    public String direction;
    /**
     * The local city.
     */
    public String city;
    /**
     * The local postcode.
     */
    public String postcode;
    /**
     * Current time in milliseconds.
     */
    public long currentTime;
    /**
     * The number a user enters or leaves an area.
     */
    public int number;

    public GeolocationCallbackData() {
        this.TIME_CREATED = System.currentTimeMillis();
    }

    public void setLatLon(LatLon latLon) {
        this.latLon = latLon;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setEventType(String eventType) {
        this.EVENT_TYPE = eventType;
    }

}
