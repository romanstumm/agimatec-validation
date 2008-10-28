package com.agimatec.validation.jsr303;

import javax.validation.Constraint;
import javax.validation.ConstraintFactory;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 13:18:36 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class DefaultConstraintFactory implements ConstraintFactory {
    /**
     * Instantiate a Constraint.
     *
     * @return Returns a new Constraint instance
     *         The ConstraintFactory is <b>not</b> responsible for calling Constraint#initialize
     */
    public <T extends Constraint> T getInstance(Class<T> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
