package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 18.11.2009 <br/>
 * Time: 14:02:57 <br/>
 * Copyright: Agimatec GmbH
 */
public class NotEmptyValidatorForMap implements ConstraintValidator<NotEmpty, Map> {
    public void initialize(NotEmpty constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Map value, ConstraintValidatorContext context) {
        return value == null || !value.isEmpty();
    }
}
