package io.github.privacysecurer.image;

import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.location.LatLon;

/**
 * Detect face in an image.
 */
class ImageFaceDetector extends ImageProcessor<Boolean> {

    ImageFaceDetector(String imageDataField) {
        super(imageDataField);
    }

    @Override
    protected Boolean processImage(UQI uqi, ImageData imageData) {
        return imageData.hasFace(uqi);
    }

}
