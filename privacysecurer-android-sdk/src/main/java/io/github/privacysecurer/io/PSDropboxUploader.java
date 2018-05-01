package io.github.privacysecurer.io;

import android.Manifest;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.DropboxUtils;

/**
 * Upload an item to Dropbox
 */

final class PSDropboxUploader<Tin> extends PSFileWriter<Tin> {

    PSDropboxUploader(Function<Tin, String> filePathGenerator, boolean append) {
        super(filePathGenerator, false, append);
        this.addRequiredPermissions(Manifest.permission.INTERNET);
    }

    @Override
    public void applyInBackground(UQI uqi, Tin input) {
        super.applyInBackground(uqi, input);
        DropboxUtils.addToWaitingList(uqi, this.validFilePath);
        DropboxUtils.syncFiles(uqi, this.append);
    }
}
