package com.agimatec.validation.constraints;

import com.agimatec.validation.jsr303.example.Address;

import javax.validation.Constraint;
import javax.validation.ConstraintContext;

/**
 * Description: Class not implemented! <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 11:45:22 <br/>
 */
public class ZipCodeCityCoherenceConstraint implements Constraint<ZipCodeCityCoherenceChecker> {
    public void initialize(ZipCodeCityCoherenceChecker constraintAnnotation) {
    }

    public boolean isValid(Object value, ConstraintContext context) {
        boolean r = true;
        Address adr = (Address) value;
        if ("error".equals(adr.getZipCode())) {
            context.disableDefaultError();
            context.addError("zipcode not OK");
            r = false;
        }
        if ("error".equals(adr.getCity())) {
            context.disableDefaultError();
            context.addError("city not OK", "city");
            r = false;
        }
        return r;
    }
}
