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
import java.util.ArrayList;
import java.util.List;

import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.location.Geolocation;
import io.github.privacysecurer.location.GeolocationOperators;
import io.github.privacysecurer.location.LatLon;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Location related events, used for setting event parameters and providing processing methods.
 */
public class GeolocationEvent extends EventType {

    // Field name options
    public static final String LatLon = "latlon";
    public static final String Speed = "speed";
    public static final String City = "city";
    public static final String Postcode = "postcode";
    public static final String Direction = "direction";
    public static final String Distance = "distance";

    // Operator options
    public static final String IN = "in";
    public static final String OUT = "out";
    public static final String CROSSES = "crosses";
    public static final String UPDATED = "updated";
    public static final String GTE = "gte";
    public static final String LTE = "lte";
    public static final String GT = "gt";
    public static final String LT = "lt";
    public static final String EQ = "eq";
    public static final String NEQ = "neq";

    /**
     * The boolean flag used to indicate whether the event is triggered or not,
     * its initial value is false.
     */
    public boolean satisfyCond = false;
    private BroadListener broadListener;

    /**
     * Event type defined in Event class.
     */
    private String eventType;
    /**
     * The field name of personal data.
     */
    private String fieldName;
    /**
     * The operators on the field value.
     */
    private String comparator;
    /**
     * The speed threshold in m/s.
     */
    private Double threshold;
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
     * The location granularity level.
     */
    private String locationPrecision;
    /**
     * The interval of location updating in milliseconds.
     */
    private long interval;
    /**
     * The event recurrence times, could be 0 representing that events happen uninterruptedly,
     * also positive value representing that events happen limited times, especially value 1
     * meaning that events happen only once.
     */
    private Integer recurrence;
    /**
     * A matrix setting the sampling interval and location precision in various sections, the elements
     * in each row are upper bound, lower bound, interval and precision in turn.
     */
    private List<List> optimizationMatrix = new ArrayList<>();

    // the city detected last time
    String lastCity;
    // the postcode detected last time
    String lastPostcode;
    // the direction detected last time
    String lastDirection;
    // the boolean flag detected last time
    Boolean lastGeofence;
    // used to count the event occurrence times
    static int counter = 0;

    private static Object monitor = new Object();
    private static boolean isCharged = false;
    private boolean broadcastRegistered = false;
    private boolean crossesFirstExecute = true;
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
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    @Override
    public String getComparator() {
        return this.comparator;
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
    public void setMaxNumberOfRecurrences(Integer recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public Integer getMaxNumberOfRecurrences() {
        return this.recurrence;
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
    public void and(List<EventType> andEvents) {

    }

    @Override
    public List<EventType> getAndEvents() {
        return null;
    }

    @Override
    public void or(List<EventType> orEvents) {

    }

    @Override
    public List<EventType> getOrEvents() {
        return null;
    }

    @Override
    public void not(List<EventType> notEvents) {

    }

    @Override
    public List<EventType> getNotEvents() {
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
    public void addOptimizationConstraints(List<List> optimizationMatrix) {
        this.optimizationMatrix = optimizationMatrix;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handle(final Context context, final EventCallback eventCallback) {
        UQI uqi = new UQI(context);
        mContext = context;
        final GeolocationCallbackData geolocationCallbackData = new GeolocationCallbackData();

        // Get the current battery level
        BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
        int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        // Add interval and precise settings based on the battery level
        if (optimizationMatrix != null) {
            for (int i=0; i<optimizationMatrix.size(); i++) {
                if ((Integer)optimizationMatrix.get(i).get(0) >= batteryLevel &&
                        (Integer)optimizationMatrix.get(i).get(1) <= batteryLevel) {
                    if (optimizationMatrix.get(i).get(2) != EventType.Off) {
                        interval = (Long) optimizationMatrix.get(i).get(2);

                        if (optimizationMatrix.get(i).get(3) != EventType.DefaultPrecision)
                            locationPrecision = (String) optimizationMatrix.get(i).get(3);
                    }
                    else {
                        // get current charging status
                        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = context.registerReceiver(null, intentFilter);
                        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                status == BatteryManager.BATTERY_STATUS_FULL;

                        // if the device is charging, just sample data immediately, otherwise
                        // sleep until it is charged.
                        if (!isCharging) {
                            Log.d("Log", "Event will be paused until getting charged.");
                            new WaitThread().start();
                            new NotifyThread().start();
                        }
                    }
                    // If found a satisfied section, just break the loop. In this way,
                    // the boundary value could also be processed appropriately.
                    break;
                }
            }
        }

        switch (fieldName) {
            case LatLon:
                if (placeName != null) {
                    this.setEventType(EventType.Geolocation_Check_Place);
                } else {
                    if (comparator.equals(UPDATED))
                        this.setEventType(EventType.Geolocation_Updated);
                    else
                        this.setEventType(EventType.Geolocation_Fence);
                }
                break;
            case Speed:
                this.setEventType(EventType.Geolocation_Check_Speed);
                break;
            case City:
                this.setEventType(EventType.Geolocation_Change_City);
                break;
            case Postcode:
                this.setEventType(EventType.Geolocation_Change_Postcode);
                break;
            case Direction:
                this.setEventType(EventType.Geolocation_Turning);
                break;
            case Distance:
                this.setEventType(EventType.Geolocation_Arrive_Destination);
                break;
            default:
                Log.d("Log", "No matchable event type, please check it again.");
        }
        geolocationCallbackData.setEventType(eventType);

        switch (eventType) {
            case EventType.Geolocation_Fence:
                periodicEvent = true;

                final PStreamProvider pStreamProvider = Geolocation.asUpdates(interval, Geolocation.LEVEL_EXACT);
                uqi.getData(pStreamProvider, Purpose.UTILITY("Listen to GeoFence periodically."))
                        .setField("geoFence", GeolocationOperators.inCircle(Geolocation.LAT_LON, latitude, longitude, radius))
                        .forEach("geoFence", new Callback<Boolean>() {
                            @Override
                            protected void onInput(Boolean geoFence) {

                                switch (comparator) {
                                    case IN:
                                        if (geoFence) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Location is in the region.");
                                                Toast.makeText(context, "In the region!", Toast.LENGTH_SHORT).show();
                                                geolocationCallbackData.setCurrentTime(System.currentTimeMillis());
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case OUT:
                                        if (!geoFence) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Location isn't in the region.");
                                                Toast.makeText(context, "Out of the region!", Toast.LENGTH_SHORT).show();
                                                geolocationCallbackData.setCurrentTime(System.currentTimeMillis());
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case CROSSES:
                                        if (geoFence.equals(lastGeofence)) {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        } else {

                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider.isCancelled = true;
                                            } else {

                                                // Initially geofence isn't equal to lastGeofence, but it doesn't mean the user crosses
                                                // the area, thus we use a boolean flag to deal with this situation.
                                                if (!crossesFirstExecute) {
                                                    Log.d("Log", "Entered or leaved the geofence.");
                                                    Toast.makeText(context, "Cross the region!", Toast.LENGTH_SHORT).show();
                                                    geolocationCallbackData.setNumber(counter);
                                                    eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                    setSatisfyCond();
                                                }
                                                crossesFirstExecute = false;
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

            case EventType.Geolocation_Check_Place:
                periodicEvent = true;
                // Get the latitude and longitude from the place name
                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses;
                LatLon latLon = null;

                try {
                    addresses = geocoder.getFromLocationName(placeName, 5);
                    if (addresses == null) return;
                    Address location = addresses.get(0);
                    latLon = new LatLon(location.getLatitude(), location.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // default radius settings, as radius is easy to be ignored by developers
                if (radius == null) this.setRadius(100.0);

                final PStreamProvider pStreamProvider1 = Geolocation.asUpdates(interval, Geolocation.LEVEL_EXACT);
                uqi.getData(pStreamProvider1, Purpose.UTILITY("Listen to the user local location."))
                        .setField("distance", GeolocationOperators.arriveDestination(Geolocation.LAT_LON, latLon))
                        .forEach("distance", new Callback<Double>() {
                            @Override
                            protected void onInput(Double distance) {
                                if (comparator.equals(IN)) {

                                    if (distance <= radius) {
                                        counter++;
                                        if (recurrence != EventType.AlwaysRepeat && counter > recurrence)
                                            pStreamProvider1.isCancelled = true;
                                        else {
                                            Log.d("Log", "In " + placeName + ".");
                                            geolocationCallbackData.setCurrentTime(System.currentTimeMillis());
                                            eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                            setSatisfyCond();
                                        }
                                    } else {
                                        Log.d("Log", "Event hasn't happened yet.");
                                        satisfyCond = false;
                                    }

                                } else {
                                    if (distance > radius) {
                                        counter++;
                                        if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                            pStreamProvider1.isCancelled = true;
                                        } else {
                                            Log.d("Log", "Out of " + placeName + ".");
                                            geolocationCallbackData.setCurrentTime(System.currentTimeMillis());
                                            eventCallback.setGeolocationCallbackData(geolocationCallbackData);
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

            case EventType.Geolocation_Check_Speed:
                periodicEvent = true;
                BigDecimal dThreshold = new BigDecimal(String.valueOf(threshold));
                final Float fThreshold = dThreshold.floatValue();

                final PStreamProvider pStreamProvider2 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider2, Purpose.UTILITY("Listen to over speed."))
                        .forEach(Geolocation.SPEED, new Callback<Float>() {
                            @Override
                            protected void onInput(Float speed) {

                                switch (comparator) {
                                    case GTE:
                                        if (speed >= fThreshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider2.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Speed is greater than or equal to the threshold.");
                                                Toast.makeText(context, " Over speed! ", Toast.LENGTH_SHORT).show();
                                                geolocationCallbackData.setSpeed(speed);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LTE:
                                        if (speed <= fThreshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider2.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Speed is lower than or equal to the threshold.");
                                                Toast.makeText(context, " Under speed! ", Toast.LENGTH_SHORT).show();
                                                geolocationCallbackData.setSpeed(speed);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case GT:
                                        if (speed > fThreshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider2.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Speed is greater than the threshold.");
                                                geolocationCallbackData.setSpeed(speed);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LT:
                                        if (speed < fThreshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider2.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Speed is lower than the threshold.");
                                                geolocationCallbackData.setSpeed(speed);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case EQ:
                                        if (speed.equals(fThreshold)) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider2.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Speed is equal to the threshold.");
                                                geolocationCallbackData.setSpeed(speed);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case NEQ:
                                        if (!speed.equals(fThreshold)) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider2.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Speed isn't equal to the threshold.");
                                                geolocationCallbackData.setSpeed(speed);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;
                                }
                            }
                        });
                break;

            case EventType.Geolocation_Arrive_Destination:
                periodicEvent = true;
                LatLon destination = new LatLon(latitude, longitude);

                final PStreamProvider pStreamProvider3 = Geolocation.asUpdates(interval, locationPrecision);
                uqi.getData(pStreamProvider3, Purpose.UTILITY("Listen to the distance to destination"))
                        .setField("distance", GeolocationOperators.arriveDestination(Geolocation.LAT_LON, destination))
                        .forEach("distance", new Callback<Double>() {
                            @Override
                            protected void onInput(Double distance) {

                                switch (comparator) {
                                    case GTE:
                                        if (distance >= threshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider3.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Distance is greater than or equal to the threshold.");
                                                geolocationCallbackData.setDistance(distance);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LTE:
                                        if (distance <= threshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider3.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Distance is lower than or equal to the threshold.");
                                                geolocationCallbackData.setDistance(distance);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case GT:
                                        if (distance > threshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider3.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Distance is greater than the threshold.");
                                                geolocationCallbackData.setDistance(distance);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case LT:
                                        if (distance < threshold) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider3.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Distance is lower than the threshold.");
                                                geolocationCallbackData.setDistance(distance);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case EQ:
                                        if (distance.equals(threshold)) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider3.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Distance is equal to the threshold.");
                                                geolocationCallbackData.setDistance(distance);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                    case NEQ:
                                        if (!distance.equals(threshold)) {
                                            counter++;
                                            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                                pStreamProvider3.isCancelled = true;
                                            } else {
                                                Log.d("Log", "Distance isn't equal to the threshold.");
                                                geolocationCallbackData.setDistance(distance);
                                                eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                                setSatisfyCond();
                                            }
                                        } else {
                                            Log.d("Log", "Event hasn't happened yet.");
                                            satisfyCond = false;
                                        }
                                        break;

                                }

                            }
                        });
                break;

            case EventType.Geolocation_Change_City:
                periodicEvent = true;

                final PStreamProvider pStreamProvider4 = Geolocation.asUpdates(interval, Geolocation.LEVEL_CITY);
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
                                    if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                        pStreamProvider4.isCancelled = true;
                                    } else {
                                        Log.d("Log", "City updated.");
                                        geolocationCallbackData.setCity(city);
                                        eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                        setSatisfyCond();
                                    }
                                }
                                lastCity = city;

                            }
                        });
                break;

            case EventType.Geolocation_Change_Postcode:
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
                                    if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                        pStreamProvider5.isCancelled = true;
                                    } else {
                                        Log.d("Log", "Postcode updated.");
                                        geolocationCallbackData.setPostcode(postcode);
                                        eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                        setSatisfyCond();
                                    }
                                }
                                lastPostcode = postcode;

                            }
                        });
                break;

            case EventType.Geolocation_Turning:
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
                                    if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                        pStreamProvider6.isCancelled = true;
                                    } else {
                                        Log.d("Log", "Direction updated");
                                        geolocationCallbackData.setDirection(direction);
                                        eventCallback.setGeolocationCallbackData(geolocationCallbackData);
                                        setSatisfyCond();
                                    }
                                }
                                lastDirection = direction;

                            }
                        });
                break;

            case EventType.Geolocation_Updated:
                periodicEvent = true;

                final PStreamProvider pStreamProvider7 = Geolocation.asUpdates(interval, Geolocation.LEVEL_EXACT);
                uqi.getData(pStreamProvider7, Purpose.UTILITY("Listen to location updates"))
                        .onChange(Geolocation.LAT_LON, new Callback<LatLon>() {
                            @Override
                            protected void onInput(LatLon input) {

                                counter++;
                                satisfyCond = false;
                                if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                                    pStreamProvider7.isCancelled = true;
                                } else {
                                    Log.d("Log", "Location updated");
                                    geolocationCallbackData.setLatLon(input);
                                    eventCallback.setGeolocationCallbackData(geolocationCallbackData);
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
     * Builder pattern used to construct location related events.
     */
    public static class GeolocationEventBuilder {
        private String eventDescription;
        private String fieldName;
        private String comparator;
        private Double threshold;
        private Double latitude;
        private Double longitude;
        private Double radius;
        private String placeName;
        private String locationPrecision;
        private long interval;
        private Integer recurrence;
        List<List> optimizationMatrix = new ArrayList<>();

        public GeolocationEventBuilder setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public GeolocationEventBuilder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public GeolocationEventBuilder setComparator(String comparator) {
            this.comparator = comparator;
            return this;
        }

        public GeolocationEventBuilder setThreshold(Double threshold) {
            this.threshold = threshold;
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

        public GeolocationEventBuilder setLocationPrecision(String locationPrecision) {
            this.locationPrecision = locationPrecision;
            return this;
        }

        public GeolocationEventBuilder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public GeolocationEventBuilder setMaxNumberOfRecurrences(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public GeolocationEventBuilder addOptimizationConstraints(int upperBound, int lowerBound, long intervalInSections, String precisionInSections) {
            List rowVector = new ArrayList<>();
            rowVector.add(upperBound);
            rowVector.add(lowerBound);
            rowVector.add(intervalInSections);
            rowVector.add(precisionInSections);
            optimizationMatrix.add(rowVector);
            return this;
        }

        public EventType build() {
            GeolocationEvent geolocationEvent = new GeolocationEvent();

            if (fieldName != null) {
                geolocationEvent.setFieldName(fieldName);
            }

            if (comparator != null) {
                geolocationEvent.setComparator(comparator);
            }

            if (threshold != null) {
                geolocationEvent.setThreshold(threshold);
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

            if (locationPrecision != null) {
                geolocationEvent.setLocationPrecision(locationPrecision);
            }

            if (interval != 0) {
                geolocationEvent.setInterval(interval);
            }

            if (recurrence != null) {
                geolocationEvent.setMaxNumberOfRecurrences(recurrence);
            }

            if (optimizationMatrix != null) {
                geolocationEvent.addOptimizationConstraints(optimizationMatrix);
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
