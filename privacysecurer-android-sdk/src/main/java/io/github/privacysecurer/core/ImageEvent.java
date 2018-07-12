package io.github.privacysecurer.core;


import android.content.Context;
import android.database.ContentObserver;
import android.os.FileObserver;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.util.List;

import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.image.Image;
import io.github.privacysecurer.image.ImageOperators;
import io.github.privacysecurer.utils.Consts;

import static io.github.privacysecurer.utils.Assertions.notNull;

/**
 * Image related events, used for setting event parameters and providing processing methods.
 */
public class ImageEvent<TValue> extends EventType {

    // Field name options
//    public static final String MediaLibrary = "mediaLibrary";
//    public static final String FileOrFolder = "fileOrFolder";
//    public static final String Images = "images";

    // Operator options
    public static final String UPDATED = "updated";
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
     * The field value calculation function.
     */
    private Function<Item, TValue> fieldCalculationFunction;
    /**
     * The operator on the field value.
     */
    private String comparator;
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
//    static FilesObserver filesObserver;
    FilesObserver filesObserver;

    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public <T> void setField(String fieldName, Function<Item, T> fieldCalculationFunction) {
        this.fieldName = fieldName;
        this.fieldCalculationFunction = (Function<Item, TValue>) fieldCalculationFunction;
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

    }

    @Override
    public long getInterval() {
        return 0;
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
    public void addOptimizationConstraints(List<List> batteryIntervalMatrix) {

    }

    @Override
    public void handle(Context context, EventCallback eventCallback) {
        UQI uqi = new UQI(context);
        Boolean booleanFlag = null;
        this.context = context;
        ImageCallbackData imageCallbackData = new ImageCallbackData();
        imageCallbackData.setEventType(eventType);

        switch (eventType) {
            case EventType.Image_Content_Updated:
                periodicEvent = true;

                if (fieldName == null) Log.d(Consts.LIB_TAG, "You haven't set field yet, it couldn't be null.");
                if (comparator == null) Log.d(Consts.LIB_TAG, "You haven't set comparator yet, it couldn't be null.");
                if (recurrence == null) Log.d(Consts.LIB_TAG, "You haven't set recurrence yet, it couldn't be null.");

                context.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true, imagesObserver);
                break;

            case EventType.Image_Has_Face:
                periodicEvent = false;

                if (path.isEmpty())
                    Log.d(Consts.LIB_TAG, "Path doesn't exist.");
                else
                    Log.d(Consts.LIB_TAG, path+" is being analyzed...");

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
                        Log.d(Consts.LIB_TAG, "Detect faces in the image.");
                        setSatisfyCond();
                    } else {
                        Log.d(Consts.LIB_TAG, "Detect no faces in the image.");
                    }
                } else {
                    Log.d(Consts.LIB_TAG, "Please check internal storage, nothing detected.");
                }
                break;

            case Image_File_Updated:
                periodicEvent = true;

                if (fieldName == null) Log.d(Consts.LIB_TAG, "You haven't set field yet, it couldn't be null.");
                if (comparator == null) Log.d(Consts.LIB_TAG, "You haven't set comparator yet, it couldn't be null.");
                if (path == null) Log.d(Consts.LIB_TAG, "You haven't set file or folder path yet, it couldn't be null.");
                if (recurrence == null) Log.d(Consts.LIB_TAG, "You haven't set recurrence yet, it couldn't be null.");

                if (path.isEmpty())
                    Log.d(Consts.LIB_TAG, "Path doesn't exist.");
                else
                    Log.d(Consts.LIB_TAG, path+" is being monitored...");
                filesObserver = new FilesObserver(path);
                filesObserver.startWatching();
                break;

            default:
                Log.d(Consts.LIB_TAG, "No image event matches your input, please check it.");
        }

    }

    /**
     * Builder pattern used to construct image related events.
     */
    public static class ImageEventBuilder<TValue> {
        private String eventDescription;
        private String fieldName;
        private Function<Item, TValue> fieldCalculationFunction;
        private String functionName;
        private String comparator;
        private String path;
        private Integer recurrence;

        public ImageEventBuilder setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public <Tout> ImageEventBuilder setField(String fieldName, Function<Item, Tout> fieldCalculationFunction) {
            this.fieldName = fieldName;
            this.fieldCalculationFunction = (Function<Item, TValue>) notNull("fieldCalculationFunction", fieldCalculationFunction);
            this.functionName = fieldCalculationFunction.getClass().getSimpleName();
            return this;
        }

        public ImageEventBuilder setComparator(String comparator) {
            this.comparator = comparator;
            return this;
        }

        public ImageEventBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public ImageEventBuilder setMaxNumberOfRecurrences(Integer recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public EventType build() {
            ImageEvent imageEvent = new ImageEvent();

            if (fieldName != null) {
                imageEvent.setField(fieldName, fieldCalculationFunction);
            }

            if (comparator != null) {
                imageEvent.setComparator(comparator);
            }

            if (path != null) {
                imageEvent.setPath(path);
            }

            if (recurrence != null) {
                imageEvent.setMaxNumberOfRecurrences(recurrence);
            }

            // Judge event type
            switch (functionName) {
                case "ImageDataGetter":
                    if (path != null)
                        imageEvent.setEventType(EventType.Image_File_Updated);
                    else
                        imageEvent.setEventType(EventType.Image_Content_Updated);
                    break;
                default:
                    Log.d(Consts.LIB_TAG, "No matchable event type, please check it again.");
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
            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                //Log.d(Consts.LIB_TAG, "No notification will be returned, the monitoring thread has been stopped.");
                context.getContentResolver().unregisterContentObserver(imagesObserver);
            } else {
                Log.d(Consts.LIB_TAG,"Image content are changed.");
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
            if (recurrence != EventType.AlwaysRepeat && counter > recurrence) {
                filesObserver.stopWatching();
            } else {
                if (event == FileObserver.DELETE) {
                    Log.d(Consts.LIB_TAG, "A file was deleted from the monitored directory.");
                }
                if (event == FileObserver.MODIFY) {
                    Log.d(Consts.LIB_TAG, "Data was written to a file.");
                }
                if (event == FileObserver.CREATE) {
                    Log.d(Consts.LIB_TAG, "A new file or subdirectory was created under the monitored directory.");
                }
                // A file or subdirectory was moved from the monitored directory
                if (event == FileObserver.MOVED_FROM) {
                    Log.d(Consts.LIB_TAG, "A file or subdirectory was moved from the monitored directory.");
                }
                if (event == FileObserver.ACCESS) {
                    Log.d(Consts.LIB_TAG, "Data was read from a file.");
                }
                setSatisfyCond();
            }
        }
    }

}
