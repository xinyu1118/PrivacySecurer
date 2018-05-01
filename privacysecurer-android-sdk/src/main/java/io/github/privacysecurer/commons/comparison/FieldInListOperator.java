package io.github.privacysecurer.commons.comparison;

import android.util.Log;

import java.util.List;

/**
 * Make equality-related comparisons on field value with a list.
 */
final class FieldInListOperator<TValue> extends ItemFieldListOperator<TValue> {
    final static String OPERATOR_EQ = "$field_eq";
    final static String OPERATOR_NE = "$field_ne";

    FieldInListOperator(final String operator, final String field, final List<TValue> valueToCompare) {
        super(operator, field, valueToCompare);
    }

    @Override
    protected boolean testField(TValue fieldValue) {
        boolean flag = false;
        if (valueToCompare == null) {
            Log.d("Log", "Provided lists are null.");
        }

        switch (this.operator) {
            case OPERATOR_EQ:
                for (TValue t : valueToCompare) {
                    if (equals(fieldValue, t)) {
                        flag = true;
                        break;
                    }
                }
                return flag;

            case OPERATOR_NE:
                for (TValue t : valueToCompare) {
                    if (equals(fieldValue, t)) {
                        flag = true;
                        break;
                    }
                }
                return !flag;

            default:
                throw new IllegalArgumentException("illegal operator: " + this.operator);
        }
    }

    private static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if ((object1 == null) || (object2 == null)) {
            return false;
        }
        return object1.equals(object2);
    }
}