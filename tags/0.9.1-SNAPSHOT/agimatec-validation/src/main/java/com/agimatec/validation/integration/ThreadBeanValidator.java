package com.agimatec.validation.integration;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.model.ValidationContext;

/**
 * Description: Validatator that puts the current validation
 * context into a {@link ThreadLocal} <br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 13:49:11 <br/>
 * Copyright: Agimatec GmbH 2008
 *
 * @see ThreadValidationContext
 */
public class ThreadBeanValidator extends BeanValidator {

    @Override
    protected ValidationContext createContext() {
        ThreadValidationContext context = ThreadValidationContext.getCurrent();
        if (context == null) {
            context = new ThreadValidationContext(createResults());
            ThreadValidationContext.setCurrent(context);
        }
        return context;
    }
}
