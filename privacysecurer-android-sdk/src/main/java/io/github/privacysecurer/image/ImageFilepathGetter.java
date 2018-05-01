package io.github.privacysecurer.image;

import io.github.privacysecurer.core.UQI;

/**
 * Get the file path of the image specified by an ImageData field.
 */
class ImageFilepathGetter extends ImageProcessor<String> {

    ImageFilepathGetter(String imageDataField) {
        super(imageDataField);
    }

    @Override
    protected String processImage(UQI uqi, ImageData imageData) {
        return imageData.getFilepath(uqi);
    }

}
