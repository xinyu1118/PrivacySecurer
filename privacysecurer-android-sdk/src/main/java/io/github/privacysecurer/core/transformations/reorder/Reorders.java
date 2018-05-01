package io.github.privacysecurer.core.transformations.reorder;

import io.github.privacysecurer.core.PStreamTransformation;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

/**
 * A helper class to access reorder functions.
 */
@PSOperatorWrapper
public class Reorders {
    /**
     * Sort the items in stream by the value of a field.
     *
     * @param fieldToSort the name of the field to reorder by.
     * @return the function.
     */
    public static PStreamTransformation sortBy(String fieldToSort) {
        return new ByFieldStreamSorter(fieldToSort);
    }

    /**
     * Shuffle the order of the items in stream.
     *
     * @return the function.
     */
    public static PStreamTransformation shuffle() {
        return new StreamShuffler();
    }

    /**
     * Reverse the order of the items in stream.
     *
     * @return the function.
     */
    public static PStreamTransformation reverse() {
        return new StreamReverser();
    }
}
