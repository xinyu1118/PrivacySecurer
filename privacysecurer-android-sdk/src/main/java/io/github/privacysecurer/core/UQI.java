package io.github.privacysecurer.core;


import android.content.Context;
import android.util.Log;

import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;
import io.github.privacysecurer.utils.Logging;
import io.github.privacysecurer.utils.PermissionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The unified query interface for all kinds of personal data.
 * You will need to construct an UQI with `UQI uqi = new UQI(context);`
 * To get a stream of personal data, simply call `uqi.getData` with a Stream provider.
 */
public class UQI {

    private Map<Function<Void, ?>, Purpose> provider2Purpose;
    private Set<Function<Void, Void>> queries;

    private Purpose getPurposeOfQuery(Function<Void, Void> query) {
        return this.provider2Purpose.get(query.getHead());
    }

    private transient Context context;
    public Context getContext() {
        return this.context;
    }
    public void setContext(Context context) { this.context = context; }

    private transient PSException exception;
    public PSException getException() {
        return exception;
    }

    public UQI(Context context) {
        this.context = context;
        this.provider2Purpose = new HashMap<>();
        this.queries = new HashSet<>();
    }

    /**
     * Get a PStream from a provider with a purpose.
     * For example, using `uqi.getData(Contact.getLogs(), Purpose.FEATURE("..."))`
     * will return a stream of contacts.
     * @param pStreamProvider the function to provide the personal data stream,
     *                        e.g. Geolocation.asUpdates().
     * @param purpose the purpose of personal data use, e.g. Purpose.ADS("xxx").
     * @return a multi-item stream
     */
    public PStream getData(PStreamProvider pStreamProvider, Purpose purpose) {
        this.provider2Purpose.put(pStreamProvider, purpose);
        return new PStream(this, pStreamProvider);
    }

    /**
     * Add a listener and handler for the event specified by developers, once it happens
     * notification will be returned to developers.
     * @param event the event to be listened to
     * @param callback callback with intermediate data
     */
    public void addEventListener(final EventType event, final EventCallback callback) {
        event.handle(context, callback);
        // Monitor boolean variable, whose initialization value is false.
        // If the event happens, its value is set to be true, and developers
        // could get notification and also obtain intermediate data.
        if (event.periodicEvent) {
            event.setBroadListener(new BroadListener() {
                @Override
                public void onSuccess() {
                    //Log.d("Log", "Event satisfies conditions.");

                    switch (event.getEventType()) {
                        case EventType.Audio_Check_Average_Loudness:case EventType.Audio_Check_Average_Loudness_Periodically:case EventType.Audio_Has_Human_Voice:
                        case EventType.Audio_Check_Maximum_Loudness:case EventType.Audio_Check_Maximum_Loudness_Periodically:
                            callback.onEvent(callback.getAudioCallbackData());
                            break;
                        case EventType.Geolocation_Fence:case EventType.Geolocation_Check_Place:case EventType.Geolocation_Updated:
                        case EventType.Geolocation_Check_Speed:case EventType.Geolocation_Arrive_Destination:case EventType.Geolocation_Turning:
                        case EventType.Geolocation_Change_City:case EventType.Geolocation_Change_Postcode:
                            callback.onEvent(callback.getGeolocationCallbackData());
                            break;
                        case EventType.Call_Check_Unwanted:case EventType.Call_In_List:case EventType.Call_Logs_Checking:case EventType.Call_Coming_In:
                        case EventType.Contact_Emails_In_Lists:case EventType.Contact_Lists_Updated:
                            callback.onEvent(callback.getContactCallbackData());
                            break;
                        case EventType.Message_Check_Unwanted:case EventType.Message_In_List:case EventType.Message_Coming_In:
                        case EventType.Message_Lists_Updated:
                            callback.onEvent(callback.getMessageCallbackData());
                            break;
                        case EventType.Image_Content_Updated:case EventType.Image_File_Updated:case EventType.Image_Has_Face:
                            callback.onEvent(callback.getImageCallbackData());
                            break;
                        case EventType.Aggregative_Event:
                            callback.onEvent(callback.getAudioCallbackData());
                            callback.onEvent(callback.getGeolocationCallbackData());
                            callback.onEvent(callback.getContactCallbackData());
                            callback.onEvent(callback.getMessageCallbackData());
                            callback.onEvent(callback.getImageCallbackData());
                            break;
                    }

                }
                @Override
                public void onFail(String msg) {
                    Log.d("Log", "Receive fail response.");
                }
            });
        } else {

            switch (event.getEventType()) {
                case EventType.Audio_Check_Average_Loudness:case EventType.Audio_Check_Average_Loudness_Periodically:case EventType.Audio_Has_Human_Voice:
                case EventType.Audio_Check_Maximum_Loudness:case EventType.Audio_Check_Maximum_Loudness_Periodically:
                    callback.onEvent(callback.getAudioCallbackData());
                    break;
                case EventType.Geolocation_Fence:case EventType.Geolocation_Check_Place:case EventType.Geolocation_Updated:
                case EventType.Geolocation_Check_Speed:case EventType.Geolocation_Arrive_Destination:case EventType.Geolocation_Turning:
                case EventType.Geolocation_Change_City:case EventType.Geolocation_Change_Postcode:
                    callback.onEvent(callback.getGeolocationCallbackData());
                    break;
                case EventType.Call_Check_Unwanted:case EventType.Call_In_List:case EventType.Call_Logs_Checking:case EventType.Call_Coming_In:
                case EventType.Contact_Emails_In_Lists:case EventType.Contact_Lists_Updated:
                    callback.onEvent(callback.getContactCallbackData());
                    break;
                case EventType.Message_Check_Unwanted:case EventType.Message_In_List:case EventType.Message_Coming_In:
                case EventType.Message_Lists_Updated:
                    callback.onEvent(callback.getMessageCallbackData());
                    break;
                case EventType.Image_Content_Updated:case EventType.Image_File_Updated:case EventType.Image_Has_Face:
                    callback.onEvent(callback.getImageCallbackData());
                    break;
                case EventType.Aggregative_Event:
                    callback.onEvent(callback.getAudioCallbackData());
                    callback.onEvent(callback.getGeolocationCallbackData());
                    callback.onEvent(callback.getContactCallbackData());
                    callback.onEvent(callback.getMessageCallbackData());
                    callback.onEvent(callback.getImageCallbackData());
                    break;
            }

        }
    }

    /**
     * Stop all query in this UQI.
     */
    public void stopAll() {
        Logging.debug("Trying to stop all privacysecurer Queries.");

        this.exception = PSException.INTERRUPTED("Stopped by app.");
        for (Function<Void, Void> query : queries) {
            query.cancel(this);
        }
    }

    private transient Map<Function<Void, PStream>, PStream> reusedMProviders = new HashMap<>();
    private transient Set<Function<Void, ? extends Stream>> evaluatedProviders = new HashSet<>();

    /**
     * Reuse a PStream.
     * @param stream the stream to reuse.
     */
    void reuse(PStream stream, int numOfReuses) {
        Function<Void, PStream> reusedProvider = stream.getStreamProvider();
        stream.receiverCount = numOfReuses;
        reusedMProviders.put(reusedProvider, stream);
    }

    private boolean tryReuse(Function<Void, Void> query) {
        if (reusedMProviders != null) {
            for (Function<Void, PStream> provider : reusedMProviders.keySet()) {
                if (query.startsWith(provider)) {
                    Function<? super PStream, Void> newQuery = query.removeStart(provider);
                    if (!this.evaluatedProviders.contains(provider)) {
                        PStream stream = reusedMProviders.get(provider);
                        PStream newStream = provider.apply(this, null);
                        newStream.receiverCount = stream.receiverCount;
                        reusedMProviders.put(provider, newStream);
                        evaluatedProviders.add(provider);
                    }
                    newQuery.apply(this, reusedMProviders.get(provider));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Evaluate current UQI.
     *
     * @param query the query to evaluate.
     * @param retry whether to try again if the permission is denied.
     */
    public void evaluate(Function<Void, Void> query, boolean retry) {
        Logging.debug("Trying to evaluate query {" + query + "}. Purpose {" + this.getPurposeOfQuery(query) + "}");
        Logging.debug("Required Permissions: " + query.getRequiredPermissions());

        this.queries.add(query);

        if (PermissionUtils.checkPermissions(this.context, query.getRequiredPermissions())) {
            Logging.debug("Permission granted, evaluating...");
            boolean reused = this.tryReuse(query);
            if (!reused) query.apply(this, null);
            Logging.debug("Evaluated.");
        }
        else if (retry) {
            // If retry is true, try to request permissions
            Logging.debug("Permission denied, retrying...");
            PermissionUtils.requestPermissionAndEvaluate(this, query);
        }
        else {
            // If retry is false, cancel all functions.
            Logging.debug("Permission denied, cancelling...");
            Set<String> deniedPermissions = PermissionUtils.getDeniedPermissions(this.context, query.getRequiredPermissions());
            this.exception = PSException.PERMISSION_DENIED(deniedPermissions);
            query.cancel(this);
//            this.context = null; // remove context
            Logging.debug("Cancelled.");
        }
    }

    /**
     * Cancel a query with an exception.
     */
    void cancelQueriesWithException(Function<?, ?> function, PSException exception) {
        this.exception = exception;
        for (Function<Void, Void> query : this.queries) {
            if (query.containsFunction(function)) query.cancel(this);
        }
    }
}
