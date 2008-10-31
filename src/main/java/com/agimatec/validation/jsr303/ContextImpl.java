package com.agimatec.validation.jsr303;

import com.agimatec.validation.ValidationResults.Error;
import com.agimatec.validation.model.ValidationContext;

import javax.validation.Context;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: implementation of context provided to  <br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 10:52:27 <br/>
 * Copyright: Agimatec GmbH
 */
class ContextImpl implements Context {
    protected final ValidationContext vContext;
    protected boolean defaultDisabled;
    protected final List<Error> errors;
    protected final ConstraintValidation validation;

    ContextImpl(ValidationContext validationContext, ConstraintValidation aConstraintValidation) {
        vContext = validationContext;
        errors = new ArrayList<Error>(5);
        validation = aConstraintValidation;
    }

    public void disableDefaultError() {
        defaultDisabled = true;
    }

    public String getDefaultErrorMessage() {
        return (String) validation.getParameters().get("message");
    }

    public void addError(String message) {
        /**
         * on property-level use the default property as context information
         */
        errors.add(new Error(message, vContext.getBean(), vContext.getPropertyName()));
    }

    public void addError(String message, String property) {
        /**
         * throw ValidationException when the property is not present
         * on the bean level object
         */
        if (null == vContext.getMetaBean().getProperty(property)) {
            throw new ValidationException(
                    "property {" + property + "} is not present on bean level object");
        }
        errors.add(new Error(message, vContext.getBean(), property));
    }

    protected List<Error> getErrors() {
        if (!defaultDisabled) {
            // use default property on property-level:
            errors.add(new Error(getDefaultErrorMessage(), vContext.getBean(),
                    vContext.getPropertyName()));
            defaultDisabled = false;
        }
        return errors;
    }
}
