package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

/**
 * valid when object is NOT null
 */
public class NotNullValidator implements ConstraintValidator<NotNull, Object> {
    public void initialize(NotNull constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value != null;
    }
}
