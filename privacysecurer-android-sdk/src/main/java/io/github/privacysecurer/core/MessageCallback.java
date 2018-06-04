package io.github.privacysecurer.core;


import java.util.List;

import io.github.privacysecurer.location.LatLon;

/**
 * Message related callbacks with intermediate data.
 */
public abstract class MessageCallback extends PSCallback {

    /**
     * Intermediate data to be called back, the caller of incoming messages.
     */
    private static String caller;


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
    public void setCurrentTime(long currentTime) {

    }

    @Override
    public long getCurrentTime() {
        return 0;
    }

    @Override
    public void setSpeed(Float speed) {

    }

    @Override
    public Float getSpeed() {
        return null;
    }

    @Override
    public void setCity(String city) {

    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public void setPostcode(String postcode) {

    }

    @Override
    public String getPostcode() {
        return null;
    }

    @Override
    public void setDirection(String direction) {

    }

    @Override
    public String getDirection() {
        return null;
    }

    @Override
    public void setNumber(int number) {

    }

    @Override
    public int getNumber() {
        return 0;
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
        this.caller = caller;
    }

    @Override
    public String getCaller() {
        return this.caller;
    }

    @Override
    public void setLatLon(LatLon latLon) {

    }

    @Override
    public LatLon getLatLon() {
        return null;
    }

    @Override
    public void setDistance(Double distance) {

    }

    @Override
    public Double getDistance() {
        return null;
    }

    @Override
    public void setCallRecords(List<Item> callRecords) {

    }

    @Override
    public List<Item> getCallRecords() {
        return null;
    }

}
