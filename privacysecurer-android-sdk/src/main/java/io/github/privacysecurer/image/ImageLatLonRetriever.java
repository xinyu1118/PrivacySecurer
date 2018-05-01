package io.github.privacysecurer.image;

import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.location.LatLon;

/**
 * Retrieve the latitude and longitude of the image.
 */
class ImageLatLonRetriever extends ImageProcessor<LatLon> {

    ImageLatLonRetriever(String imageDataField) {
        super(imageDataField);
    }

    @Override
    protected LatLon processImage(UQI uqi, ImageData imageData) {
        return imageData.getLatLon(uqi);
    }

}
