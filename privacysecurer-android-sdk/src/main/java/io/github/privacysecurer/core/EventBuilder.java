package io.github.privacysecurer.core;


import java.util.List;

public abstract class EventBuilder {

    public abstract EventBuilder setEventType(String eventType);

    public abstract EventBuilder setRecurring(Integer recurringNumber);

    public abstract EventBuilder setDuration(long duration);

    public abstract EventBuilder setInterval(long interval);

    public abstract EventBuilder setThreshold(Double threshold);

    public abstract EventBuilder setLocationPrecision(String locationPrecision);

    public abstract EventBuilder setLatitude(Double latitude);

    public abstract EventBuilder setLongitude(Double longitude);

    public abstract EventBuilder setRadius(Double radius);

    public abstract EventBuilder setPlaceName(String placeName);

    public abstract EventBuilder setLists(List<String> lists);

    public abstract EventBuilder setCaller(String caller);

    public abstract EventBuilder setPath(String path);

    public abstract EventBuilder and(Event andEvent);

    public abstract EventBuilder or(Event orEvent);

    public abstract EventBuilder not(Event notEvent);

    public abstract Event build();
}
