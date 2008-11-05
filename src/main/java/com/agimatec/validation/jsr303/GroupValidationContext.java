package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.ValidationContext;

import javax.validation.Constraint;
import javax.validation.ConstraintDescriptor;
import javax.validation.MessageResolver;
import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 10:15:08 <br/>
 * Copyright: Agimatec GmbH
 */
interface GroupValidationContext extends ValidationContext {
    List<String> getSequencedGroups();

    String getPropertyPath();

    void setCurrentGroup(String currentGroup);

    String getCurrentGroup();

    void setCurrentConstraint(ConstraintDescriptor constraint);

    ConstraintDescriptor getCurrentConstraint();

    /**
     * Clear map of validated objects (invoke when you want to 'reuse' the
     * context for different validations)
     */
    void resetValidated();

    void setFixedValue(Object value);

    String[] DEFAULT_GROUPS = {"default"};

    MessageResolver getMessageResolver();

    boolean collectValidated(Object bean, Constraint constraint);
}
