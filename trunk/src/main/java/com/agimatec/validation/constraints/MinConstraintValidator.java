package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 */
public class MinConstraintValidator implements ConstraintValidator<Min> {
    private int min;

    public void initialize(Min constraintAnnotation) {
        min = constraintAnnotation.value();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return ((Number) value).intValue() >= min;
    }
}
