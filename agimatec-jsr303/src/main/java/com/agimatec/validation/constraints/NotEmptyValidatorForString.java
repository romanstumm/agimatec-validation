package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 18.11.2009 <br/>
 * Time: 14:03:22 <br/>
 * Copyright: Agimatec GmbH
 */
public class NotEmptyValidatorForString implements ConstraintValidator<NotEmpty, String> {
    public void initialize(NotEmpty constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.length() > 0;
    }
}
