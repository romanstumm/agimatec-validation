package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: not implemented (test only)<br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 16:38:25 <br/>
 * Copyright: Agimatec GmbH
 */
public class FrenchZipcodeValidator implements ConstraintValidator<FrenchZipCode> {
    public void initialize(FrenchZipCode constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object object, ConstraintValidatorContext validationContext) {
        return null != object;
    }
}
