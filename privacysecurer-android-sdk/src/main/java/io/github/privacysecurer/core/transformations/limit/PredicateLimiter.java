package io.github.privacysecurer.core.transformations.limit;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;

import static io.github.privacysecurer.utils.Assertions.notNull;

/**
 * Limit the items with a predicate, stop the stream once the predicate fails.
 */
final class PredicateLimiter extends StreamLimiter {
    private final Function<Item, Boolean> predicate;

    PredicateLimiter(final Function<Item, Boolean> predicate) {
        this.predicate = notNull("predicate", predicate);
        this.addParameters(predicate);
    }

    @Override
    protected boolean keep(Item item) {
        return this.predicate.apply(this.getUQI(), item);
    }

    private static final String OPERATOR = "$limit_with_predicate";

}
