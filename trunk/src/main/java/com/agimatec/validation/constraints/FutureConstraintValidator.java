package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Future;
import java.util.Calendar;
import java.util.Date;

/**
 * Description: validate a date or calendar representing a date in the future <br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:43 <br/>
 * Copyright: Agimatec GmbH
 */
public class FutureConstraintValidator implements ConstraintValidator<Future, Object> {

    public void initialize(Future constraintAnnotation) {
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (value instanceof Date) {
            return ((Date) value).after(now());
        } else if (value instanceof Calendar) {
            return ((Calendar) value).getTime().after(now());
        } else {
            return false;
        }
    }

    /**
     * overwrite when you need a different algorithm for 'now'.
     *
     * @return current date/time
     */
    protected Date now() {
        return new Date();
    }
}