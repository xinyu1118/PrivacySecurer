package io.github.privacysecurer.device;

import android.Manifest;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.DeviceUtils;

/**
 * Get device id
 */
class WifiStatusChecker extends Function<Void, Boolean> {

    WifiStatusChecker() {
        this.addRequiredPermissions(Manifest.permission.ACCESS_WIFI_STATE);
    }

    @Override
    public Boolean apply(UQI uqi, Void input) {
        return DeviceUtils.isWifiConnected(uqi.getContext());
    }
}
