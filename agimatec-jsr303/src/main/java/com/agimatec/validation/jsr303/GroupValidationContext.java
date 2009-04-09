package com.agimatec.validation.jsr303;

import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.model.ValidationContext;

import javax.validation.ConstraintDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 10:15:08 <br/>
 * Copyright: Agimatec GmbH
 */
interface GroupValidationContext extends ValidationContext, MessageInterpolator.Context  {
    /**
     * the groups in their sequence for validation
     * @return
     */
    Groups getGroups();

    void setCurrentGroup(Group group);

    Group getCurrentGroup();

    String getPropertyPath();

    void setConstraintDescriptor(ConstraintDescriptor constraint);

    /**
     * Clear map of validated objects (invoke when you want to 'reuse' the
     * context for different validations)
     */
    void resetValidated();

    void setFixedValue(Object value);

    MessageInterpolator getMessageResolver();

    boolean collectValidated(Object bean, ConstraintValidator constraint);
}
