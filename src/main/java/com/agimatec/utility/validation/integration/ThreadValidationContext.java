package com.agimatec.utility.validation.integration;

import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.ValidationResults;

/**
 * Description: Used to bundle {@link ValidationContext} and {@link ValidationResults}
 * together and bind them to the current thread.
 * Use this class when you need to append validation errors in service layers
 * without handing a ValidationContext and/or ValidationResults instance
 * through your method signatures.<br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 13:41:10 <br/>
 *
 */
public class ThreadValidationContext extends ValidationContext {
    protected static final ThreadLocal<ThreadValidationContext> current =
            new ThreadLocal<ThreadValidationContext>();

    private ValidationResults validationResults;

    public static ThreadValidationContext getCurrent() {
        return current.get();
    }

    public static void setCurrent(ThreadValidationContext aValidationContext) {
        if (aValidationContext == null) {
            current.remove();
        } else {
            current.set(aValidationContext);
        }
    }

    public ValidationResults getValidationResults() {
        return validationResults;
    }

    public void setValidationResults(ValidationResults validationResults) {
        this.validationResults = validationResults;
    }
}
