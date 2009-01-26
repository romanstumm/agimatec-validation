package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 */
public class NotNullConstraintValidator implements ConstraintValidator<NotNull> {
    public void initialize(NotNull constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value != null;
    }
}
