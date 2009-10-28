package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 28.10.2009 <br/>
 * Time: 15:08:50 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecEmailValidator implements ConstraintValidator<AgimatecEmail,String> {
    public void initialize(AgimatecEmail agimatecEmail) {
        // do nothing
    }

    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return true;
    }
}
