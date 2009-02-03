package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Null;

/**
 * Description: valid when object is null<br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:49:08 <br/>
 * Copyright: Agimatec GmbH
 */
public class NullConstraintValidator implements ConstraintValidator<Null, Object> {

	public void initialize(Null constraintAnnotation) {
        // do nothing
    }

	public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
		return object == null;
	}
}
