package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 18:13:37 <br/>
 */
public class CreditCardConstraintValidator implements ConstraintValidator<CreditCard> {
    public void initialize(CreditCard constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // TODO RSt - not implemented, just an example
        return true;
    }
}
