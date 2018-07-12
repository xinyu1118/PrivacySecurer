package io.github.privacysecurer.communication;


import java.util.List;

import io.github.privacysecurer.commons.ItemOperator;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.UQI;
import io.github.privacysecurer.core.exceptions.PSException;
import io.github.privacysecurer.core.purposes.Purpose;

/**
 * Get a list of contact items.
 */
class ContactListsGetter extends ItemOperator<List<Item>> {

    @Override
    public List<Item> apply(UQI uqi, Item input) {
        List<Item> contactLists = null;
        try {
            contactLists = uqi.getData(Contact.getAll(), Purpose.UTILITY("Get contact lists."))
                    .asList();
        } catch (PSException e) {
            e.printStackTrace();
        }
        return contactLists;
    }
}

