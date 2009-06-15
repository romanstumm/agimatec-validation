package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertFalse;

/**
 * Description: assert that value is false<br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:05 <br/>
 * Copyright: Agimatec GmbH
 */
public class AssertFalseValidator implements ConstraintValidator<AssertFalse, Boolean> {

	public void initialize(AssertFalse constraintAnnotation) {
	}

	public boolean isValid(Boolean value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || !value;
    }

}