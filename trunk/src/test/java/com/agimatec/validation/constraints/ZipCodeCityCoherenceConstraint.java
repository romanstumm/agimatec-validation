package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Context;

/**
 * Description: Class not implemented! <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 11:45:22 <br/>
 *
 */
public class ZipCodeCityCoherenceConstraint implements Constraint<ZipCodeCityCoherenceChecker> {
    public void initialize(ZipCodeCityCoherenceChecker constraintAnnotation) {
        // TODO RSt - nyi
    }

    public boolean isValid(Object value, Context context) {
        return true;  // TODO RSt - nyi
    }
}
