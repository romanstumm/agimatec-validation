package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
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
public class NotEmptyConstraintValidator implements ConstraintValidator<NotEmpty, Object>/*, StandardConstraint */{
    public void initialize(NotEmpty constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        } else if (value instanceof Collection) {
            return ((Collection) value).size() > 0;
        } else if (value instanceof Map) {
            return ((Map) value).size() > 0;
        } else if(value instanceof String) {
            return ((String) value).length() > 0;
        } else {
            throw new IllegalArgumentException(value + " is of unsupported type");
        }
    }

/*    public StandardConstraintDescriptor getStandardConstraints() {
        return new StandardConstraintDescriptor() {
            @Override
            public Boolean getNullability() {
                return false;
            }
        };
    }*/
}
