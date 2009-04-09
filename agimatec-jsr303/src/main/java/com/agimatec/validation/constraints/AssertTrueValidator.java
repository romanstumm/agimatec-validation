package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertTrue;

/**
 * Description: assert that value is true<br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:21 <br/>
 * Copyright: Agimatec GmbH
 */
public class AssertTrueValidator implements ConstraintValidator<AssertTrue, Boolean> {

	public void initialize(AssertTrue constraintAnnotation) {
	}

	public boolean isValid(Boolean value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || value;
    }

}