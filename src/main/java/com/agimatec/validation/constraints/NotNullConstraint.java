package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintContext;

/**
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 */
public class NotNullConstraint implements Constraint<NotNull> {
    public void initialize(NotNull constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value, ConstraintContext context) {
        return value != null;
    }
}
