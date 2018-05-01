package io.github.privacysecurer.core;


import java.util.List;

import io.github.privacysecurer.location.LatLon;

/**
 * Geolocation related callbacks with intermediate data.
 */
public abstract class GeolocationCallback extends PSCallback {

    /**
     * Count the duration of stay in milliseconds.
     */
    private static long durationOfStay;
    /**
     * The current speed in m/s.
     */
    private static Float speed;
    /**
     * The local city.
     */
    private static String city;
    /**
     * The local postcode.
     */
    private static String postcode;
    /**
     * The current direction, left or right.
     */
    private static String direction;
    /**
     * The number a user enters or leaves an area.
     */
    private static int number;
    /**
     * The local LatLon, including latitude and longitude.
     */
    private static LatLon latLon;
    /**
     * The distance to the destination.
     */
    private static Double distance;


    @Override
    public void setAvgLoudness(Double avgLoudness) {

    }

    @Override
    public Double getAvgLoudness() {
        return null;
    }

    @Override
    public void setMaxLoudness(Double maxLoudness) {

    }

    @Override
    public Double getMaxLoudness() {
        return null;
    }

    @Override
    public void setDurationOfStay(long durationOfStay) {
        this.durationOfStay = durationOfStay;
    }

    @Override
    public long getDurationOfStay() {
        return this.durationOfStay;
    }

    @Override
    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    @Override
    public Float getSpeed() {
        return this.speed;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getPostcode() {
        return this.postcode;
    }

    @Override
    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String getDirection() {
        return this.direction;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    @Override
    public void setEmails(List<String> emails) {

    }

    @Override
    public List<String> getEmails() {
        return null;
    }

    @Override
    public void setCaller(String caller) {

    }

    @Override
    public String getCaller() {
        return null;
    }

    @Override
    public void setLatLon(LatLon latLon) {
        this.latLon = latLon;
    }

    @Override
    public LatLon getLatLon() {
        return this.latLon;
    }

    @Override
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public Double getDistance() {
        return this.distance;
    }

}
