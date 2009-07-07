package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.routines.EMailValidation;

import javax.validation.Constraint;

/**
 * <p/>
 * --
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * --
 * <p/>
 * Description: pattern for validation taken from hibernate.<br/>
 * User: roman.stumm <br/>
 * Date: 14.10.2008 <br/>
 * Time: 12:38:37 <br/>
 * Copyright: Agimatec GmbH
 */
public class EmailValidator implements Constraint<Email> {
    protected final EMailValidation validation = new EMailValidation();

    public boolean isValid(Object value) {
        return validation.isValid(value);
    }

    public void initialize(Email parameters) {
        // do nothing (as long as Email has no properties)
    }
}