package io.github.privacysecurer.accessibility;

import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.core.PStreamProvider;
import io.github.privacysecurer.utils.annotations.PSItem;
import io.github.privacysecurer.utils.annotations.PSItemField;

/**
 * Browser search activity.
 */
@PSItem
public class BrowserSearch extends Item {
    /**
     * The searched text.
     */
    @PSItemField(type = String.class)
    public static final String TEXT = "text";

    BrowserSearch(String title) {
        this.setFieldValue(TEXT, title);
    }

    /**
     * Provide a live stream of BrowserSearch items.
     * An item will be generated once the user do a search in the browser.
     *
     * @return the provider function
     */
    // @RequiresPermission(value = Manifest.permission.BIND_ACCESSIBILITY_SERVICE)
    public static PStreamProvider asUpdates(){
        return new BrowserSearchEventsProvider();
    }

}
