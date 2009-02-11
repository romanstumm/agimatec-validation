package com.agimatec.validation.constraints;

import com.agimatec.validation.jsr303.example.Address;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: Class not implemented! <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 11:45:22 <br/>
 */
public class ZipCodeCityCoherenceValidator
      implements ConstraintValidator<ZipCodeCityCoherence, Address> {
    public void initialize(ZipCodeCityCoherence constraintAnnotation) {
    }

    public boolean isValid(Address adr, ConstraintValidatorContext context) {
        boolean r = true;
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
