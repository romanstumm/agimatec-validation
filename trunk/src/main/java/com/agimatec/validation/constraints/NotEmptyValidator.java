package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Description:  Check the non emptyness of the element (array, collection, map, string) 
 **/
public class NotEmptyValidator
      implements ConstraintValidator<NotEmpty, Object> {
    public void initialize(NotEmpty constraintAnnotation) {
        // do nothing
    }

    // enhancement: extend to support any object that has a public isEmpty():boolean method?
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        } else if (value instanceof Collection) {
            return !((Collection) value).isEmpty();
        } else if (value instanceof Map) {
            return !((Map) value).isEmpty();
        } else if (value instanceof String) {
            return ((String) value).length() > 0;
        } else {
            throw new IllegalArgumentException(value + " is of unsupported type");
        }
    }
}
