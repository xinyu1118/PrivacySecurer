package io.github.privacysecurer.utils.annotations;

import java.lang.annotation.Documented;

/**
 * An annotation representing a action function.
 */
@Documented
public @interface PSAction {
    boolean blocking();
}
