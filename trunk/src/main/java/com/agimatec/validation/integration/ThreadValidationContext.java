package com.agimatec.validation.integration;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.ValidationListener;

/**
 * Description: Used to bind the current validation context to the current thread.
 * Use this class when you need to append validation errors in service layers
 * without handing a ValidationContext and/or ValidationResults instance
 * through your method signatures.<br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 13:41:10 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ThreadValidationContext extends BeanValidationContext {
    protected static final ThreadLocal<ThreadValidationContext> current =
            new ThreadLocal<ThreadValidationContext>();

    public ThreadValidationContext(ValidationListener listener) {
        super(listener);
    }

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
}
