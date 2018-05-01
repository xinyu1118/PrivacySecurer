package io.github.privacysecurer.notification;

import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.core.PStreamProvider;
import io.github.privacysecurer.utils.PermissionUtils;

abstract class NotificationEventProvider extends PStreamProvider {

    NotificationEventProvider() {
        this.addRequiredPermissions(PermissionUtils.USE_NOTIFICATION_SERVICE);
    }

    private boolean registered = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void provide() {
        PSNotificationListenerService.registerProvider(this);
        registered = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCancel(UQI uqi) {
        PSNotificationListenerService.unregisterProvider(this);
        registered = false;
    }

    public abstract void handleNotificationEvent(StatusBarNotification sbn, String action);
}
