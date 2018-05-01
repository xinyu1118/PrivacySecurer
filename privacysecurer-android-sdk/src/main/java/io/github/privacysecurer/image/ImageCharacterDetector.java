package io.github.privacysecurer.image;

import io.github.privacysecurer.core.UQI;

/**
 * Detect face in an image.
 */
class ImageCharacterDetector extends ImageProcessor<Boolean> {

    ImageCharacterDetector(String imageDataField) {
        super(imageDataField);
    }

    @Override
    protected Boolean processImage(UQI uqi, ImageData imageData) {
        return imageData.hasCharacter(uqi);
    }

}
