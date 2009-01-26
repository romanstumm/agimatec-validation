package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.ValidationContext;

import javax.validation.ConstraintDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import javax.validation.groups.Default;
import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 10:15:08 <br/>
 * Copyright: Agimatec GmbH
 */
interface GroupValidationContext extends ValidationContext {
    List<Class<?>> getSequencedGroups();

    String getPropertyPath();

    void setCurrentGroup(Class<?> currentGroup);

    Class<?> getCurrentGroup();

    void setCurrentConstraint(ConstraintDescriptor constraint);

    ConstraintDescriptor getCurrentConstraint();

    /**
     * Clear map of validated objects (invoke when you want to 'reuse' the
     * context for different validations)
     */
    void resetValidated();

    void setFixedValue(Object value);

    Class<?>[] DEFAULT_GROUPS = {Default.class};

    MessageInterpolator getMessageResolver();

    boolean collectValidated(Object bean, ConstraintValidator constraint);
}
