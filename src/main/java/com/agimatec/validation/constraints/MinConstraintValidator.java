package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * Description: validate that number-value of passed object is >= min-value<br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:54 <br/>
 * Copyright: Agimatec GmbH
 */
public class MinConstraintValidator implements ConstraintValidator<Min, Object> {
    private long min;

    public void initialize(Min constraintAnnotation) {
        min = constraintAnnotation.value();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if(value instanceof Number) {
            return ((Number)value).longValue() >= min;
        } else {
            return new BigDecimal(String.valueOf(value)).longValue() >= min;
        }
    }
}
