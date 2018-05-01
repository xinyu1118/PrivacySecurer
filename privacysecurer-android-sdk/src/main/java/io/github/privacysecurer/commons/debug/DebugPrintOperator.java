package io.github.privacysecurer.commons.debug;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.utils.Logging;

/**
 * Print the item for debugging
 */

final class DebugPrintOperator<Tin> extends Function<Tin, Void> {
    @Override
    public Void apply(UQI uqi, Tin input) {
        String debugMsg;

        if (input instanceof Item) debugMsg = ((Item) input).toDebugString();
        else debugMsg = "" + input;

        Logging.debug(debugMsg);
        return null;
    }

}
