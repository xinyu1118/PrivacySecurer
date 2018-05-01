package io.github.privacysecurer.core.transformations.filter;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.Assertions;

/**
 * Only keep the items whose field value is changed after last item.
 */
final class KeepFieldChangesFilter<TValue> extends StreamFilter {
    private final String fieldName;

    KeepFieldChangesFilter(String fieldName) {
        this.fieldName = Assertions.notNull("fieldName", fieldName);
    }

    private transient TValue lastFieldValue;

    @Override
    public boolean keep(Item item) {
        TValue fieldValue = item.getValueByField(this.fieldName);
        if (fieldValue == null) {
            if (lastFieldValue == null)
                return false;
        } else if (fieldValue.equals(lastFieldValue)) {
            return false;
        }
        this.lastFieldValue = fieldValue;
        return true;
    }

}
