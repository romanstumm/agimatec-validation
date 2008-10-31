package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Context;
import javax.validation.StandardConstraint;
import javax.validation.StandardConstraintDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Description:  Check the non emptyness of the element.
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 */
public class NotEmptyConstraint implements Constraint<NotEmpty>, StandardConstraint {
    public void initialize(NotEmpty constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value, Context context) {
        if (value == null) return false;
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        } else if (value instanceof Collection) {
            return ((Collection) value).size() > 0;
        } else if (value instanceof Map) {
            return ((Map) value).size() > 0;
        } else {
            return ((String) value).length() > 0;
        }
    }

    public StandardConstraintDescriptor getStandardConstraints() {
        return new StandardConstraintDescriptor() {
            @Override
            public Boolean getNullability() {
                return false;
            }
        };
    }
}
