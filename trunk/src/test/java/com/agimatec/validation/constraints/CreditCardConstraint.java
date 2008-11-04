package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintContext;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 18:13:37 <br/>
 */
public class CreditCardConstraint implements Constraint<CreditCard> {
    public void initialize(CreditCard constraintAnnotation) {
        // do nothing
    }

    public boolean isValid(Object value, ConstraintContext context) {
        // TODO RSt - not implemented, just an example
        return true;
    }
}
