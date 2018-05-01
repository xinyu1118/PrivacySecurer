package io.github.privacysecurer.utils.annotations;

import java.lang.annotation.Documented;

/**
 * An annotation representing an item field
 */
@Documented
public @interface PSItemField {
    Class type();
}
