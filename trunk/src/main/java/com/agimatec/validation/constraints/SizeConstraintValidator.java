package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Description: Validate strings, arrays, maps and collections for Size constraint <br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:49:24 <br/>
 * Copyright: Agimatec GmbH
 */
public class SizeConstraintValidator implements ConstraintValidator<Size, Object> {
    private int min;
    private int max;

    /**
     * Configure the constraint validator based on the elements
     * specified at the time it was defined.
     *
     * @param constraint the constraint definition
     */
    public void initialize(Size constraint) {
        min = constraint.min();
        max = constraint.max();
    }

    /**
     * Validate a specified value.
     * returns false if the specified value does not conform to the definition
     *
     * @throws IllegalArgumentException if the object is not supported type
     */
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int size;
        if (value instanceof String) {
            size = ((String) value).length();
        } else if (value instanceof Collection) {
            size = ((Collection) value).size();
        } else if (value instanceof Map) {
            size = ((Map) value).size();
        } else if (value instanceof Array) {
            size = Array.getLength(value);
        } else {
            throw new IllegalArgumentException("Unsupported type :" + value.getClass());
        }
        return size >= min && size <= max;
    }

}