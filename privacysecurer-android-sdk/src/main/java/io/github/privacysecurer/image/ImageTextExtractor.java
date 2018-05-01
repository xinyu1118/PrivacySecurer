package io.github.privacysecurer.image;

import io.github.privacysecurer.core.UQI;

/**
 * Detect face in an image.
 */
class ImageTextExtractor extends ImageProcessor<String> {

    ImageTextExtractor(String imageDataField) {
        super(imageDataField);
    }

    @Override
    protected String processImage(UQI uqi, ImageData imageData) {
        return imageData.detectText(uqi);
    }

}
