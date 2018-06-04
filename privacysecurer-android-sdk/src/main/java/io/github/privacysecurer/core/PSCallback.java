package io.github.privacysecurer.core;

import java.util.List;

import io.github.privacysecurer.location.LatLon;

/**
 * Abstract callback class, setting and getting parameters are implemented by subclasses.
 */
public abstract class PSCallback {

    // Audio related methods for setting and getting intermediate data.
    public abstract void setAvgLoudness(Double avgLoudness);
    public abstract Double getAvgLoudness();
    public abstract void setMaxLoudness (Double maxLoudness);
    public abstract Double getMaxLoudness();

    // Geolocation related methods for setting and getting intermediate data.
    public abstract void setCurrentTime(long currentTime);
    public abstract long getCurrentTime();
    public abstract void setSpeed(Float speed);
    public abstract Float getSpeed();
    public abstract void setCity(String city);
    public abstract String getCity();
    public abstract void setPostcode(String postcode);
    public abstract String getPostcode();
    public abstract void setDirection(String direction);
    public abstract String getDirection();
    public abstract void setNumber(int number);
    public abstract int getNumber();
    public abstract void setLatLon(LatLon latLon);
    public abstract LatLon getLatLon();
    public abstract void setDistance(Double distance);
    public abstract Double getDistance();

    // Contact and Call related methods for setting and getting intermeidate data.
    public abstract void setCallRecords(List<Item> callRecords);
    public abstract List<Item> getCallRecords();
    public abstract void setEmails(List<String> emails);
    public abstract List<String> getEmails();

    // Also used for message related callback methods.
    public abstract void setCaller(String caller);
    public abstract String getCaller();


    /**
     * This method will be invoked when developers would like to obtain intermediate data.
     */
    public abstract void onEvent();


}
