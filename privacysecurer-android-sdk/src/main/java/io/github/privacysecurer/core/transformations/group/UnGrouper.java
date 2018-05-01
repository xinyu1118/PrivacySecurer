package io.github.privacysecurer.core.transformations.group;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamTransformation;
import io.github.privacysecurer.utils.Assertions;
import io.github.privacysecurer.utils.HashUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Un-group a list field in each item to multiple items.
 * This un-grouper will not change the order of items.
 */
final class UnGrouper extends PStreamTransformation {
    private final String unGroupField;
    private final String newField;

    UnGrouper(String unGroupField, String newField) {
        this.unGroupField = Assertions.notNull("unGroupField", unGroupField);
        this.newField = Assertions.notNull("newField", newField);
        this.addParameters(unGroupField, newField);
    }

    @Override
    protected void onInput(Item item) {
        if (item.isEndOfStream()) {
            this.finish();
            return;
        }

        List<Object> unGroupList = item.getValueByField(this.unGroupField);
        if (unGroupList == null) return;

        for (Object newFieldValue : unGroupList) {
            Item newItem = new Item(item.toMap());
            newItem.excludeFields(unGroupField);
            newItem.setFieldValue(newField, newFieldValue);
            this.output(newItem);
        }
    }
}
