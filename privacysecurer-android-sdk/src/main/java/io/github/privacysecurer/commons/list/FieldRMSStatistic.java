package io.github.privacysecurer.commons.list;

import io.github.privacysecurer.utils.StatisticUtils;

import java.util.List;

/**
 * Calculate the RMS (root mean square) of a field in the stream.
 * If calculation fails (e.g. there is no item in the stream), the result will be null
 */
final class FieldRMSStatistic extends NumListProcessor<Double> {

    FieldRMSStatistic(String numListField) {
        super(numListField);
    }

    @Override
    protected Double processNumList(List<Number> numList) {
        return StatisticUtils.rms(numList);
    }
}
