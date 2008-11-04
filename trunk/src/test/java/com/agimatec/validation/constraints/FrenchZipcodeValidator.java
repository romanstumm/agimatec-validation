package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintContext;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 16:38:25 <br/>
 * Copyright: Agimatec GmbH
 */
public class FrenchZipcodeValidator implements Constraint<FrenchZipCode> {
    public void initialize(FrenchZipCode constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object object, ConstraintContext validationContext) {
        if (null == object) {
            return false;  // do nothing
        }
        return true;
    }
}
