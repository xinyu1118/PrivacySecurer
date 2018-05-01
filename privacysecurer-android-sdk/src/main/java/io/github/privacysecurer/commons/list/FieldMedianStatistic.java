package io.github.privacysecurer.commons.list;

import io.github.privacysecurer.utils.StatisticUtils;

import java.util.List;

/**
 * Get the median value of a field in the stream.
 */
final class FieldMedianStatistic extends NumListProcessor<Number> {

    FieldMedianStatistic(String numListField) {
        super(numListField);
    }

    @Override
    protected Number processNumList(List<Number> numList) {
        return StatisticUtils.median(numList);
    }
}
