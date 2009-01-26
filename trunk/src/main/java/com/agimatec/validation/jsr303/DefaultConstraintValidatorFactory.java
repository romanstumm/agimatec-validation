package com.agimatec.validation.jsr303;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

/**
 * Description: create constraint instances with the default / no-arg constructor <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 13:18:36 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class DefaultConstraintValidatorFactory implements ConstraintValidatorFactory {
    /**
     * Instantiate a Constraint.
     *
     * @return Returns a new Constraint instance
     *         The ConstraintFactory is <b>not</b> responsible for calling Constraint#initialize
     */
    public <T extends ConstraintValidator> T getInstance(Class<T> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
