package io.github.privacysecurer.core;

import java.util.List;

import io.github.privacysecurer.location.LatLon;

/**
 * Aggregative events related callbacks with intermediate data.
 */
public class AggregativeCallback extends PSCallback {

    /**
     * Intermediate data to be called back, average loudness in dB.
     */
    private static Double avgLoudness;
    /**
     * Intermediate data to be called back, maximum loudness in dB.
     */
    private static Double maxLoudness;
    /**
     * Current time in milliseconds.
     */
    private static long currentTime;
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
    /**
     * Intermediate data to be called back, a list of emails
     * matched with the emails in the contacts.
     */
    private static List<String> emails;
    /**
     * Intermediate data to be called back, the caller of incoming calls.
     */
    private static String caller;


    @Override
    public void setAvgLoudness(Double avgLoudness) {
        this.avgLoudness = avgLoudness;
    }

    @Override
    public Double getAvgLoudness() {
        return this.avgLoudness;
    }

    @Override
    public void setMaxLoudness(Double maxLoudness) {
        this.maxLoudness = maxLoudness;
    }

    @Override
    public Double getMaxLoudness() {
        return this.maxLoudness;
    }

    @Override
    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public long getCurrentTime() {
        return this.currentTime;
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

    @Override
    public void setCallRecords(List<Item> callRecords) {

    }

    @Override
    public List<Item> getCallRecords() {
        return null;
    }

    @Override
    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    @Override
    public List<String> getEmails() {
        return this.emails;
    }

    @Override
    public void setCaller(String caller) {
        this.caller = caller;
    }

    @Override
    public String getCaller() {
        return this.caller;
    }

    @Override
    public void onEvent() {

    }
}
