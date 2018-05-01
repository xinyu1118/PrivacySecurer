package io.github.privacysecurer.core.transformations.filter;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;

import static io.github.privacysecurer.utils.Assertions.notNull;

/**
 * Keep the items that satisfy a predicate.
 */
final class PredicateFilter extends StreamFilter {
    private final Function<Item, Boolean> predicate;

    PredicateFilter(final Function<Item, Boolean> predicate) {
        this.predicate = notNull("predicate", predicate);
        this.addParameters(predicate);
    }

    @Override
    public boolean keep(Item item) {
        return this.predicate.apply(this.getUQI(), item);
    }

}
