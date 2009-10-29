package com.agimatec.validation.constraints;

import org.apache.commons.lang.ArrayUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 29.10.2009 <br/>
 * Time: 14:41:07 <br/>
 * Copyright: Agimatec GmbH
 */
public class HasStringValidator implements ConstraintValidator<HasValue, String> {
    private String[] values;

    public void initialize(HasValue stringValues) {
        values = stringValues.value();
    }

    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || ArrayUtils.contains(values, s);
    }
}
