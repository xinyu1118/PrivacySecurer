package io.github.privacysecurer.core;


import java.util.List;

import io.github.privacysecurer.location.LatLon;

/**
 * Image related callbacks with intermediate data.
 */
public abstract class ImageCallback extends PSCallback {

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

    }

    @Override
    public long getDurationOfStay() {
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

    }

    @Override
    public String getCaller() {
        return null;
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

}
