package io.github.privacysecurer.core;


import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.List;

import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.image.Image;
import io.github.privacysecurer.image.ImageOperators;

/**
 * Image related events, used for setting event parameters and providing processing methods.
 */
public class ImageEvent extends Event {

    // Field name options
    public static final String MediaLibrary = "mediaLibrary";
    public static final String FileOrFolder = "fileOrFolder";
    public static final String Images = "images";

    // Operator options
    public static final String Updated = "updated";
    public static final String HasFace = "hasFace";

    /**
     * The boolean flag used to indicate whether the event is triggered or not,
     * the initial value is false.
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
     * The operator on the field value.
     */
    private String operator;
    /**
     * File or file folder path.
     */
    private String path;
    /**
     * The event recurrence times, could be 0 representing that events happen uninterruptedly,
     * also positive value representing that events happen limited times, especially value 1
     * meaning that events happen only once.
     */
    private Integer recurrence;

    // used to count the event occurrence times
    int counter = 0;

    private Context context;
    ImagesObserver imagesObserver = new ImagesObserver(new Handler());
    // Save FileObserver instance to a field otherwise it will be garbage collected.
    static FilesObserver filesObserver;

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

    }

    @Override
    public long getInterval() {
        return 0;
    }

    @Override
    public void setNotificationResponsiveness(Integer recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public Integer getNotificationResponsiveness() {
        return this.recurrence;
    }

    @Override
    public void setThreshold(Double threshold) {

    }

    @Override
    public Double getThreshold() {
        return null;
    }

    @Override
    public void setLocationPrecision(String locationPrecision) {

    }

    @Override
    public String getLocationPrecision() {
        return null;
    }

    @Override
    public void setLatitude(Double latitude) {

    }

    @Override
    public Double getLatitude() {
        return null;
    }

    @Override
    public void setLongitude(Double longitude) {

    }

    @Override
    public Double getLongitude() {
        return null;
    }

    @Override
    public void setRadius(Double radius) {

    }

    @Override
    public Double getRadius() {
        return null;
    }

    @Override
    public void setPlaceName(String placeName) {

    }

    @Override
    public String getPlaceName() {
        return null;
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
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
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
    public void addOptimizationConstraints(List<List> batteryIntervalMatrix) {

    }

    @Override
    public void handle(Context context, PSCallback psCallback) {
        UQI uqi = new UQI(context);
        Boolean booleanFlag = null;
        this.context = context;

        // Judge event type
        switch (fieldName) {
            case MediaLibrary:
                this.setEventType(Event.Image_Content_Updated);
                break;
            case FileOrFolder:
                this.setEventType(Event.Image_File_Updated);
                break;
            case Images:
                this.setEventType(Event.Image_Has_Face);
                break;
            default:
                Log.d("Log", "No matchable event type, please check it again.");
        }

        switch (eventType) {
            case Event.Image_Content_Updated:
                periodicEvent = true;
                context.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true, imagesObserver);
                break;

            case Event.Image_Has_Face:
                periodicEvent = false;
                if (path.isEmpty())
                    Log.d("Log", "Path doesn't exist.");
                else
                    Log.d("Log", path+" is being analyzed...");

                try {
                    booleanFlag = uqi.getData(Image.getFromStorage(), Purpose.UTILITY("Listen to detecting faces."))
                            .filter(Image.IMAGE_PATH, path)
                            .setField("imageFlag", ImageOperators.hasFace(Image.IMAGE_DATA))
                            .getFirst("imageFlag");
                } catch (PSException e) {
                    e.printStackTrace();
                }
                if (booleanFlag != null) {
                    if (booleanFlag) {
                        Log.d("Log", "Detect faces in the image.");
                        setSatisfyCond();
                    } else {
                        Log.d("Log", "Detect no faces in the image.");
                    }
                } else {
                    Log.d("Log", "Please check internal storage, nothing detected.");
                }
                break;

            case Image_File_Updated:
                periodicEvent = true;
                if (path.isEmpty())
                    Log.d("Log", "Path doesn't exist.");
                else
                    Log.d("Log", path+" is being monitored...");
                filesObserver = new FilesObserver(path);
                filesObserver.startWatching();
                break;

            default:
                Log.d("Log", "No image event matches your input, please check it.");
        }

    }

    /**
     * Builder pattern used to construct image related events.
     */
    public static class ImageEventBuilder {
        private String fieldName;
        private String operator;
        private String path;
        private Integer recurrence;

        public ImageEventBuilder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public ImageEventBuilder setOperator(String operator) {
            this.operator = operator;
            return this;
        }

        public ImageEventBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public ImageEventBuilder setNotificationResponsiveness(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public Event build() {
            ImageEvent imageEvent = new ImageEvent();

            if (fieldName != null) {
                imageEvent.setFieldName(fieldName);
            }

            if (operator != null) {
                imageEvent.setOperator(operator);
            }

            if (path != null) {
                imageEvent.setPath(path);
            }

            if (recurrence != null) {
                imageEvent.setNotificationResponsiveness(recurrence);
            }

            return imageEvent;
        }
    }

    /**
     * Inner class extends from ContentObserver, and overrides onChange() method
     * used to monitor image content changes.
     */
    private final class ImagesObserver extends ContentObserver {
        public ImagesObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != Event.ContinuousSampling && counter > recurrence) {
                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(imagesObserver);
            } else {
                Log.d("Log","Image content are changed.");
                setSatisfyCond();
            }
        }
    }

    /**
     * Inner class extends from FileObserver, and overrides onEvent() method
     * used to monitor file folder content changes, specially checking an image inserted
     * to a folder (move from or created).
     */
    private final class FilesObserver extends FileObserver {
        public FilesObserver (String path) {
            super(path, FileObserver.ALL_EVENTS);
        }
        @Override
        public void onEvent(int i, String path) {
            int event = i & FileObserver.ALL_EVENTS;
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != Event.ContinuousSampling && counter > recurrence) {
                filesObserver.stopWatching();
            } else {
                if (event == FileObserver.DELETE) {
                    Log.d("Log", "A file was deleted from the monitored directory.");
                }
                if (event == FileObserver.MODIFY) {
                    Log.d("Log", "Data was written to a file.");
                }
                if (event == FileObserver.CREATE) {
                    Log.d("Log", "A new file or subdirectory was created under the monitored directory.");
                }
                // A file or subdirectory was moved from the monitored directory
                if (event == FileObserver.MOVED_FROM) {
                    Log.d("Log", "A file or subdirectory was moved from the monitored directory.");
                }
                if (event == FileObserver.ACCESS) {
                    Log.d("Log", "Data was read from a file.");
                }
                setSatisfyCond();
            }
        }
    }

}
