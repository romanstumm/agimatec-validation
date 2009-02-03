package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;
import java.math.BigDecimal;

/**
 * Description: validate that number-value of passed object is <= max-value<br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:54 <br/>
 * Copyright: Agimatec GmbH
 */
public class MaxConstraintValidator implements ConstraintValidator<Max, Object> {
    private long max;

    public void initialize(Max constraintAnnotation) {
        max = constraintAnnotation.value();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue() <= max;
        } else {
            return new BigDecimal(String.valueOf(value)).longValue() <= max;
        }
    }
}
