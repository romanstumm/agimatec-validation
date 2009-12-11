package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 18.11.2009 <br/>
 * Time: 14:03:12 <br/>
 * Copyright: Agimatec GmbH
 */
public class NotEmptyValidatorForCollection implements ConstraintValidator<NotEmpty, Collection> {
    public void initialize(NotEmpty constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Collection value, ConstraintValidatorContext context) {
        return value == null || !value.isEmpty();
    }
}
