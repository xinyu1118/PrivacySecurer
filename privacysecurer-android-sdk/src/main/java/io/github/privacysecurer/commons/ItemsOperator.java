package io.github.privacysecurer.commons;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;

import java.util.List;

/**
 * A function that takes a list of items as input.
 */

public abstract class ItemsOperator<Tout> extends Function<List<Item>, Tout> {
}
