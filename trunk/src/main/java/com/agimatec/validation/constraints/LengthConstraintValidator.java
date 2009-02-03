package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
//import javax.validation.StandardConstraint;
//import javax.validation.StandardConstraintDescriptor;

/**
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 * Check that a string length is between min and max
 */
public class LengthConstraintValidator implements ConstraintValidator<Length, String>/*, StandardConstraint */{
    private int min;
    private int max;

    /**
     * Configure the constraint validator based on the elements
     * specified at the time it was defined.
     *
     * @param constraint the constraint definition
     */
    public void initialize(Length constraint) {
        min = constraint.min();
        max = constraint.max();
    }

    /**
     * Validate a specified value.
     * returns false if the specified value does not conform to the definition
     *
     * @throws IllegalArgumentException if the object is not of type String
     */
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int length = value.length();
        return length >= min && length <= max;
    }

}