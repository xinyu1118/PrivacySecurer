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
 * Image related events, used for setting parameters and providing event processing methods.
 */
public class ImageEvent extends Event {
    /**
     * The boolean flag used to indicate whether the event is triggered or not,
     * the initial value is false.
     */
    public boolean satisfyCond = false;
    private BroadListener broadListener;

    /**
     * Event type, define in Event class.
     */
    private String eventType;
    /**
     * The occurrence times for periodic events, e.g. for the Audio_Check_Average_Loudness_Periodically event,
     * setRecurrence(2) means that if the average loudness is higher than the threshold twice, the programming model
     * will stop monitoring the event.
     */
    private Integer recurrence;
    /**
     * File or file folder path.
     */
    private String path;

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
    public void setRecurrence(Integer recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public Integer getRecurrence() {
        return this.recurrence;
    }

    @Override
    public void setFieldName(String fieldName) {

    }

    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public void setOperator(String operator) {

    }

    @Override
    public String getOperator() {
        return null;
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
    public void handle(Context context, PSCallback psCallback) {
        UQI uqi = new UQI(context);
        Boolean booleanFlag = null;
        this.context = context;

        switch (eventType) {
            case Event.Image_Content_Updated:
                periodicEvent = true;
                context.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true, imagesObserver);
                break;

            case Event.Image_Has_Face:
                periodicEvent = false;

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
                filesObserver = new FilesObserver(path);
                filesObserver.startWatching();
                break;

            default:
                Log.d("Log", "No image event matches your input, please check it.");
        }

    }

    /**
     * Inner class used to build image related events and corresponding parameters.
     */
    public static class ImageEventBuilder {
        private String eventType;
        private String path;
        private Integer recurrence;

        public ImageEventBuilder setEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public ImageEventBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public ImageEventBuilder setRecurrence(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public Event build() {
            ImageEvent imageEvent = new ImageEvent();

            if (eventType != null) {
                imageEvent.setEventType(eventType);
            }

            if (path != null) {
                imageEvent.setPath(path);
            }

            if (recurrence != null) {
                imageEvent.setRecurrence(recurrence);
            }

            return imageEvent;
        }
    }

    /**
     * Inner class extends from ContentObserver, and overrides onChange() method,
     * used to monitoring image content changes.
     */
    private final class ImagesObserver extends ContentObserver {
        public ImagesObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != null && counter > recurrence) {
                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(imagesObserver);
            } else {
                Log.d("Log","Image content are changed.");
                setSatisfyCond();
            }
        }
    }

    /**
     * Inner class extends from FileObserver, and overrides onEvent() method,
     * used to monitoring file folder content changes, specially checking an image inserted
     * to a folder (move from or created).
     */
    private final class FilesObserver extends FileObserver {
        public FilesObserver (String path) {
            super(path, FileObserver.ALL_EVENTS);
        }

        @Override
        public void onEvent(int i, String path) {
            counter++;
            // If the event occurrence times exceed the limitation, unregister the contactsObserver
            if (recurrence != null && counter > recurrence) {
                //Log.d("Log", "No notification will be returned, the monitoring thread has been stopped.");
                filesObserver.stopWatching();
            } else {

                if (i == FileObserver.DELETE) {
                    Log.d("Log", "A file was deleted from the monitored directory.");
                }
                if (i == FileObserver.MODIFY) {
                    Log.d("Log", "Data was written to a file.");
                }
                if (i == FileObserver.CREATE) {
                    Log.d("Log", "A new file or subdirectory was created under the monitored directory.");
                }
                // A file or subdirectory was moved from the monitored directory
                if (i == FileObserver.MOVED_FROM) {
                    Log.d("Log", "A file or subdirectory was moved from the monitored directory.");
                }
                if (i == FileObserver.ACCESS) {
                    Log.d("Log", "Data was read from a file.");
                }

            }

        }
    }
}
