package io.github.privacysecurer.commons.list;

import io.github.privacysecurer.utils.StatisticUtils;

import java.util.List;

/**
 * Get the mode value of a field in the stream.
 */
final class FieldModeStatistic extends NumListProcessor<Number> {

    FieldModeStatistic(String numListField) {
        super(numListField);
    }

    @Override
    protected Number processNumList(List<Number> numList) {
        return StatisticUtils.mode(numList);
    }
}
