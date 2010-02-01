package com.agimatec.validation.jsr303.extensions;

import com.agimatec.validation.jsr303.ConstraintValidation;
import com.agimatec.validation.jsr303.ValidationCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 13:41:22 <br/>
 * Copyright: Agimatec GmbH
 */
public class ValidationCollectorGeneric implements ValidationCollector {
    private final List<ConstraintValidation> validations = new ArrayList();

    public ValidationCollectorGeneric() {
    }

    public void addValidation(ConstraintValidation validation) {
        validations.add(validation);
    }

    public List<ConstraintValidation> getValidations() {        
        return validations;
    }
}
