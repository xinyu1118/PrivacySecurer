package io.github.privacysecurer.image;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Assertions;

/**
 * Process the photo field in an item.
 */
abstract class ImageProcessor<Tout> extends ItemOperator<Tout> {

    private final String imageDataField;

    ImageProcessor(String imageDataField) {
        this.imageDataField = Assertions.notNull("imageDataField", imageDataField);
        this.addParameters(imageDataField);
    }

    @Override
    public final Tout apply(UQI uqi, Item input) {
        ImageData imageData = input.getValueByField(this.imageDataField);
        return this.processImage(uqi, imageData);
    }

    protected abstract Tout processImage(UQI uqi, ImageData imageData);
}
