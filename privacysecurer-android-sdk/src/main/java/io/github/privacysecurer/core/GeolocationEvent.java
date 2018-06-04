package io.github.privacysecurer.core;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.location.Geolocation;
import io.github.privacysecurer.location.GeolocationOperators;
import io.github.privacysecurer.location.LatLon;

import static android.content.Context.BATTERY_SERVICE;

/**
 * location related events, used for setting event parameters and providing processing methods.
 */
public class GeolocationEvent extends Event {
    public static final String Location = "location";
    public static final String Speed = "speed";
    public static final String City = "city";
    public static final String Postcode = "postcode";
    public static final String Direction = "direction";
    public static final String Distance = "distance";

    public static final String In = "in";
    public static final String Out = "out";
    public static final String InOrOut = "inOrOut";
    public static final String Updated = "updated";
    public static final String Over = "over";
    public static final String Under = "under";

    /**
     * The boolean flag used to indicate whether the event is triggered or not,
     * its initial value is false.
     */
    public boolean satisfyCond = false;
    private BroadListener broadListener;

    /**
     * Event type, defined in Event class.
     */
    private String eventType;
    /**
     * The occurrence times for periodic events, e.g. in the geofence event,
     * setRecurrence(2) means that if the user enters or leaves a certain area twice,
     * the API will stop monitoring the event.
     */
    private Integer recurrence;
    /**
     * The field name of personal data.
     */
    private String fieldName;
    /**
     * The operators on the field value.
     */
    private String operator;
    /**
     * The interval of location updating in milliseconds.
     */
    private long interval;
    /**
     * The interval of location updating in low battery level.
     */
    private long lobatInterval;
    /**
     * The upper bound of the section in low battery level.
     */
    private int upperBound;
    /**
     * The lower bound of the section in low battery level.
     */
    private int lowerBound;
    /**
     * The speed threshold in m/s.
     */
    private Double threshold;
    /**
     * The location granularity level.
     */
    private String locationPrecision;
    /**
     * The location precision granularity in low battery level.
     */
    private String lobatPrecision;
    /**
     * The location latitude.
     */
    private Double latitude;
    /**
     * The location longitude.
     */
    private Double longitude;
    /**
     * The radius of a circle area.
     */
    private Double radius;
    /**
     * The place name.
     */
    private String placeName;
    /**
     * The start time used to calculate the duration of stay.
     */
    private long startTime;
    /**
     * The stop time used to calculate the duration of stay.
     */
    private long stopTime;
    /**
     * The duration of stay in the same place.
     */
    private long durationOfStay;

    // the city detected last time
    String lastCity;
    // the postcode detected last time
    String lastPostcode;
    // the direction detected last time
    String lastDirection;
    // the boolean flag detected last time
    Boolean lastGeofence;
    // In or out of an area last time
    String lastArea = "null";
    String currentArea;
    boolean inPlaceFlag = false;
    // used to count the event occurrence times
    static int counter = 0;

    private static Object monitor = new Object();
    private static boolean isCharged = false;
    private boolean broadcastRegistered = false;
    BroadcastReceiver receiver;
    Context mContext;

    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public void setRecurrence(Integer recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public Integer getRecurrence() {
        return this.recurrence;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String getOperator() {
        return this.operator;
    }

    @Override
    public void setDuration(long duration) {

    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public long getInterval() {
        return this.interval;
    }

    @Override
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    @Override
    public Double getThreshold() {
        return this.threshold;
    }

    @Override
    public void setLocationPrecision(String locationPrecision) {
        this.locationPrecision = locationPrecision;
    }

    @Override
    public String getLocationPrecision() {
        return this.locationPrecision;
    }

    @Override
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public Double getLatitude() {
        return this.latitude;
    }

    @Override
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public Double getLongitude() {
        return this.longitude;
    }

    @Override
    public void setRadius(Double radius) {
        this.radius = radius;
    }

    @Override
    public Double getRadius() {
        return this.radius;
    }

    @Override
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public String getPlaceName() {
        return this.placeName;
    }

    @Override
    public void setLists(List<String> lists) {

    }

    @Override
    public List<String> getLists() {
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
    public void setPath(String path) {

    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void and(List<Event> andEvents) {

    }

    @Override
    public List<Event> getAndEvents() {
        return null;
    }

    @Override
    public void or(List<Event> orEvents) {

    }

    @Override
    public List<Event> getOrEvents() {
        return null;
    }

    @Override
    public void not(List<Event> notEvents) {

    }

    @Override
    public List<Event> getNotEvents() {
        return null;
    }

    @Override
    public void setSatisfyCond() {
        this.satisfyCond = true;
        if (broadListener != null) {
            if (satisfyCond) {
                broadListener.onSuccess();
            } else {
                broadListener.onFail("Receive fail response.");
            }
        }
    }

    @Override
    public boolean getSatisfyCond() {
        return this.satisfyCond;
    }

    @Override
    public void setBroadListener(BroadListener broadListener) {
        this.broadListener = broadListener;
    }

    @Override
    public void addPowerConstraints(long lobatInterval, int upperBound, int lowerBound) {
        this.lobatInterval = lobatInterval;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    @Override
    public void addPrecisionConstraints(String lobatPrecision) {
        this.lobatPrecision = lobatPrecision;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handle(final Context context, final PSCallback psCallback) {
        UQI uqi = new UQI(context);
        mContext = context;

        // add power constrains
        if (lobatInterval != 0) {
            BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
            int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            // when in low battery level, enlarge the data sampling interval and turn down location precision
            if (batteryLevel >= lowerBound && batteryLevel < upperBound) {
                interval = lobatInterval;
                if (lobatPrecision != null)
                    locationPrecision = lobatPrecision;
            }

            // when in extremely low battery level, sleep until charged.
            if (batteryLevel < lowerBound) {
                // get current charging status
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, intentFilter);
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                // if the device is charging, just sample data immediately, otherwise
                // sleep until it is charged.
                if (!isCharging) {
                    new WaitThread().start();
                    new NotifyThread().start();
                }
            }
        }

        switch (fieldName) {
            case Location:
                if (placeName != null) {
                    this.setEventType(Event.Geolocation_Check_Place);
                } else {
                    if (operator.equals(Updated))
                        this.setEventType(Event.Geolocation_Updated);
                    else
                        this.setEventType(Event.Geolocation_Fence);
                }
                break;
            case Speed:
                this.setEventType(Event.Geolocation_Check_Speed);
                break;
            case City:
                this.setEventType(Event.Geolocation_Change_City);
                break;
            case Postcode:
                this.setEventType(Event.Geolocation_Change_Postcode);
                break;
            case Direction:
                this.setEventType(Event.Geolocation_Turning);
                break;
            case Distance:
                this.setEventType(Event.Geolocation_Arrive_Destination);
                break;
            default:
                Log.d("Log", "No matchable event type, please check it again.");
        }

        switch (eventType) {
            case Event.Geolocation_Fence:
                periodicEvent = true;

                final PStreamProvider pStreamProvider = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider, Purpose.UTILITY("Listen to GeoFence periodically."))
                        .setField("geoFence", GeolocationOperators.inCircle(Geolocation.LAT_LON, latitude, longitude, radius))
                        .forEach("geoFence", new Callback<Boolean>() {
                            @Override
                            protected void onInput(Boolean geoFence) {

                                switch (operator) {
                                    case In:
                                        if (geoFence) {
                                            counter++;
                                            if (recurrence != null && counter > recurrence) {
                                                pStreamProvider.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Location is in the region.");
                                                Toast.makeText(context, "In the region!", Toast.LENGTH_SHORT).show();
                                                //psCallback.setNumber(counter);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case Out:
                                        if (!geoFence) {
                                            counter++;
                                            if (recurrence != null && counter > recurrence) {
                                                pStreamProvider.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Location isn't in the region.");
                                                Toast.makeText(context, "Out of the region!", Toast.LENGTH_SHORT).show();
                                                //psCallback.setNumber(counter);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case InOrOut:
                                        if (geoFence.equals(lastGeofence)) {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        } else {
                                            counter++;
                                            if (recurrence != null && counter > recurrence) {
                                                pStreamProvider.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Entered or leaved the geofence.");
                                                Toast.makeText(context, "In or Out of the region!", Toast.LENGTH_SHORT).show();
                                                psCallback.setNumber(counter);
                                                setSatisfyCond();
                                            }
                                        }
                                        lastGeofence = geoFence;
                                        break;

                                    default:
                                        Log.d("Log", "No matchable operator, please check it.");
                                }

                            }
                        });
                break;

            case Event.Geolocation_Check_Place:
                periodicEvent = true;
                // Get the latitude and longitude from the place name
                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses;
                LatLon latLon = null;

                try {
                    addresses = geocoder.getFromLocationName(placeName, 5);
                    if (addresses == null)
                        return;
                    Address location = addresses.get(0);
                    latLon = new LatLon(location.getLatitude(), location.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // default radius settings, as radius is easy to be ignored by developers
                if (radius == null) this.setRadius(100.0);

                final PStreamProvider pStreamProvider1 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider1, Purpose.UTILITY("Listen to the user local location."))
                        .setField("distance", GeolocationOperators.arriveDestination(Geolocation.LAT_LON, latLon))
                        .forEach("distance", new Callback<Double>() {
                            @Override
                            protected void onInput(Double distance) {
                                if (operator.equals(In)) {

                                    if (distance <= radius) {
                                        counter++;
                                        if (recurrence != null && counter > recurrence)
                                            pStreamProvider1.isCancelled = true;
                                        else {
                                            Log.d("Log", "In " + placeName + ".");
                                            psCallback.setCurrentTime(System.currentTimeMillis());
                                            setSatisfyCond();
                                        }
                                    } else {
                                        Log.d("Log", "Event hasn't happened yet.");
                                        satisfyCond = false;
                                    }

                                    /*if (distance <= radius) {
                                        currentArea = "in";
                                        counter++;
                                        if (recurrence != null && counter > recurrence) {
                                            //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                                            pStreamProvider1.isCancelled = true;
                                        } else {
                                            Log.d("Log", "In " + placeName + ".");
                                            setSatisfyCond();
                                        }
                                        // when a user enters the area first time, start counting.
                                        if (lastArea.equals("out"))
                                            startTime = System.currentTimeMillis();
                                    } else {
                                        currentArea = "out";
                                        Log.d("Log", "Event hasn't happened yet.");
                                        if (lastArea.equals("in")) {
                                            // when a user leaves the area first time, stop counting.
                                            stopTime = System.currentTimeMillis();
                                            if (startTime != 0 && stopTime >= startTime) {
                                                durationOfStay = stopTime - startTime;
                                                psCallback.setDurationOfStay(durationOfStay);
                                                inPlaceFlag = true;
                                            }
                                        }
                                    }
                                    lastArea = currentArea;*/

                                } else {
                                    if (distance > radius) {
                                        counter++;
                                        if (recurrence != null && counter > recurrence) {
                                            pStreamProvider1.isCancelled = true;
                                        } else {
                                            Log.d("Log", "Out of " + placeName + ".");
                                            psCallback.setCurrentTime(System.currentTimeMillis());
                                            setSatisfyCond();
                                        }
                                    } else {
                                        Log.d("Log", "Event hasn't happened yet.");
                                        satisfyCond = false;
                                    }

                                }
                            }
                        });
                break;

            case Event.Geolocation_Check_Speed:
                periodicEvent = true;
                BigDecimal dThreshold = new BigDecimal(String.valueOf(threshold));
                final Float fThreshold = dThreshold.floatValue();

                final PStreamProvider pStreamProvider2 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider2, Purpose.UTILITY("Listen to over speed."))
                        .forEach(Geolocation.SPEED, new Callback<Float>() {
                            @Override
                            protected void onInput(Float speed) {
                                if (operator.equals(Over)) {

                                    if (speed >= fThreshold) {
                                        counter++;
                                        if (recurrence != null && counter > recurrence) {
                                            pStreamProvider2.isCancelled = true;
                                        } else {
                                            Log.d("Log", "Over speed.");
                                            Toast.makeText(context, " Over speed! ", Toast.LENGTH_SHORT).show();
                                            psCallback.setSpeed(speed);
                                            setSatisfyCond();
                                        }
                                    } else {
                                        Log.d("Log", "Event hasn't happened yet.");
                                        satisfyCond = false;
                                    }

                                } else {

                                    if (speed < fThreshold) {
                                        counter++;
                                        if (recurrence != null && counter > recurrence) {
                                            pStreamProvider2.isCancelled = true;
                                        } else {
                                            Log.d("Log", "Under speed.");
                                            Toast.makeText(context, " Under speed! ", Toast.LENGTH_SHORT).show();
                                            psCallback.setSpeed(speed);
                                            setSatisfyCond();
                                        }
                                    } else {
                                        Log.d("Log", "Event hasn't happened yet.");
                                        satisfyCond = false;
                                    }

                                }
                            }
                        });
                break;

            case Event.Geolocation_Arrive_Destination:
                periodicEvent = true;
                LatLon destination = new LatLon(latitude, longitude);

                final PStreamProvider pStreamProvider3 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider3, Purpose.UTILITY("Listen to the distance to destination"))
                        .setField("distance", GeolocationOperators.arriveDestination(Geolocation.LAT_LON, destination))
                        .forEach("distance", new Callback<Double>() {
                            @Override
                            protected void onInput(Double distance) {

                                if (operator.equals(Under)) {
                                    // the fault tolerance 15 meters
                                    if (distance <= 15) {
                                        counter++;
                                        if (recurrence != null && counter > recurrence) {
                                            pStreamProvider3.isCancelled = true;
                                        } else {
                                            Log.d("Log", "Distance less than the radius.");
                                            psCallback.setDistance(distance);
                                            setSatisfyCond();
                                        }
                                    } else {
                                        Log.d("Log", "Event hasn't happened yet.");
                                        satisfyCond = false;
                                    }

                                } else {
                                    if (distance > 15) {
                                        counter++;
                                        if (recurrence != null && counter > recurrence) {
                                            pStreamProvider3.isCancelled = true;
                                        } else {
                                            Log.d("Log", "Distance over the radius.");
                                            psCallback.setDistance(distance);
                                            setSatisfyCond();
                                        }
                                    } else {
                                        Log.d("Log", "Event hasn't happened yet.");
                                        satisfyCond = false;
                                    }
                                }
                            }
                        });
                break;

            case Event.Geolocation_Change_City:
                periodicEvent = true;

                final PStreamProvider pStreamProvider4 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider4, Purpose.UTILITY("Listen to city change."))
                        .setField("city", GeolocationOperators.getCity(Geolocation.LAT_LON))
                        .forEach("city", new Callback<String>() {
                            @Override
                            protected void onInput(String city) {

                                if (city.equals(lastCity)) {
                                    Log.d("Log", "Event hasn't happened yet.");
                                    satisfyCond = false;
                                } else {
                                    counter++;
                                    if (recurrence != null && counter > recurrence) {
                                        pStreamProvider4.isCancelled = true;
                                    } else {
                                        Log.d("Log", "City updated.");
                                        psCallback.setCity(city);
                                        setSatisfyCond();
                                    }
                                }
                                lastCity = city;

                            }
                        });
                break;

            case Event.Geolocation_Change_Postcode:
                periodicEvent = true;

                final PStreamProvider pStreamProvider5 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider5, Purpose.UTILITY("Listen to city change."))
                        .setField("postcode", GeolocationOperators.getPostcode(Geolocation.LAT_LON))
                        .forEach("postcode", new Callback<String>() {
                            @Override
                            protected void onInput(String postcode) {

                                if (postcode.equals(lastPostcode)) {
                                    Log.d("Log", "Event hasn't happened yet.");
                                    satisfyCond = false;
                                } else {
                                    counter++;
                                    if (recurrence != null && counter > recurrence) {
                                        pStreamProvider5.isCancelled = true;
                                    } else {
                                        Log.d("Log", "Postcode updated.");
                                        psCallback.setPostcode(postcode);
                                        setSatisfyCond();
                                    }
                                }
                                lastPostcode = postcode;

                            }
                        });
                break;

            case Event.Geolocation_Turning:
                periodicEvent = true;

                final PStreamProvider pStreamProvider6 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider6, Purpose.UTILITY("Listen to making a turn."))
                        .setField("direction", GeolocationOperators.getDirection(Geolocation.BEARING))
                        .forEach("direction", new Callback<String>() {
                            @Override
                            protected void onInput(String direction) {

                                if (direction.equals(lastDirection)) {
                                    Log.d("Log", "Event hasn't happened yet.");
                                    satisfyCond = false;
                                } else {
                                    counter++;
                                    if (recurrence != null && counter > recurrence) {
                                        pStreamProvider6.isCancelled = true;
                                    } else {
                                        Log.d("Log", "Direction updated");
                                        psCallback.setDirection(direction);
                                        setSatisfyCond();
                                    }
                                }
                                lastDirection = direction;

                            }
                        });
                break;

            case Event.Geolocation_Updated:
                periodicEvent = true;

                final PStreamProvider pStreamProvider7 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider7, Purpose.UTILITY("Listen to location updates"))
                        .onChange(Geolocation.LAT_LON, new Callback<LatLon>() {
                            @Override
                            protected void onInput(LatLon input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != null && counter > recurrence) {
                                    pStreamProvider7.isCancelled = true;
                                } else {
                                    Log.d("Log", "Location updated");
                                    psCallback.setLatLon(input);
                                    setSatisfyCond();
                                }

                            }
                        });
                break;

            default:
                Log.d("Log", "No location event matches your input, please check it.");

        }

        if (broadcastRegistered)
            mContext.unregisterReceiver(receiver);

    }

    /**
     * Inner class used to build location related events and corresponding parameters.
     */
    public static class GeolocationEventBuilder {
        private String fieldName;
        private String operator;
        private long interval;
        private long lobatInterval;
        private int upperBound;
        private int lowerBound;
        private Double threshold;
        private String locationPrecision;
        private String lobatPrecision;
        private Double latitude;
        private Double longitude;
        private Double radius;
        private String placeName;
        private Integer recurrence;

        public GeolocationEventBuilder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public GeolocationEventBuilder setOperator(String operator) {
            this.operator = operator;
            return this;
        }

        public GeolocationEventBuilder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public GeolocationEventBuilder setThreshold(Double threshold) {
            this.threshold = threshold;
            return this;
        }

        public GeolocationEventBuilder setLocationPrecision(String locationPrecision) {
            this.locationPrecision = locationPrecision;
            return this;
        }

        public GeolocationEventBuilder setLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public GeolocationEventBuilder setLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public GeolocationEventBuilder setRadius(Double radius) {
            this.radius = radius;
            return this;
        }

        public GeolocationEventBuilder setPlaceName(String placeName) {
            this.placeName = placeName;
            return this;
        }

        public GeolocationEventBuilder setRecurrence(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public GeolocationEventBuilder addPowerConstraints(long lobatInterval, int upperBound, int lowerBound) {
            this.lobatInterval = lobatInterval;
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
            return this;
        }

        public GeolocationEventBuilder addPrecisionConstraints(String lobatPrecision) {
            this.lobatPrecision = lobatPrecision;
            return this;
        }

        public Event build() {
            GeolocationEvent geolocationEvent = new GeolocationEvent();

            if (fieldName != null) {
                geolocationEvent.setFieldName(fieldName);
            }

            if (operator != null) {
                geolocationEvent.setOperator(operator);
            }

            if (interval != 0) {
                geolocationEvent.setInterval(interval);
            }

            if (lobatInterval != 0 && upperBound != 0 && lowerBound != 0) {
                geolocationEvent.addPowerConstraints(lobatInterval, upperBound, lowerBound);
            }

            if (threshold != null) {
                geolocationEvent.setThreshold(threshold);
            }

            if (locationPrecision != null) {
                geolocationEvent.setLocationPrecision(locationPrecision);
            }

            if (lobatPrecision != null) {
                geolocationEvent.addPrecisionConstraints(lobatPrecision);
            }

            if (latitude != null) {
                geolocationEvent.setLatitude(latitude);
            }

            if (longitude != null) {
                geolocationEvent.setLongitude(longitude);
            }

            if (radius != null) {
                geolocationEvent.setRadius(radius);
            }

            if (placeName != null) {
                geolocationEvent.setPlaceName(placeName);
            }

            if (recurrence != null) {
                geolocationEvent.setRecurrence(recurrence);
            }

            return geolocationEvent;
        }
    }

    // If the device is not charged, wait the thread until power is connected.
    public class WaitThread extends Thread {
        public void run() {
            while(!isCharged) {
                synchronized(monitor) {
                    try {
                        monitor.wait();
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Notify the thread to keep on executing subsequent codes once charged.
    public class NotifyThread extends Thread {
        public void run() {
            IntentFilter ifilter = new IntentFilter();
            ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
            //ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                        //Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                        isCharged = true;
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }

                }
            };
            mContext.registerReceiver(receiver, ifilter);
            broadcastRegistered = true;
        }
    }
}
