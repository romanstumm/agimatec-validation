package com.agimatec.validation.jsr303;

import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.model.ValidationContext;
import com.agimatec.validation.model.MetaBean;

import javax.validation.ConstraintDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 10:15:08 <br/>
 * Copyright: Agimatec GmbH
 */
interface GroupValidationContext extends ValidationContext {
    /**
     * the groups in their sequence for validation
     * @return
     */
    Groups getGroups();

    void setCurrentGroup(Group group);

    Group getCurrentGroup();

    String getPropertyPath();
    MetaBean getRootMetaBean();

    void setConstraintDescriptor(ConstraintDescriptor constraint);
    
    public ConstraintDescriptor getConstraintDescriptor();

    public Object getValidatedValue();
    
    /**
     * Clear map of validated objects (invoke when you want to 'reuse' the
     * context for different validations)
     */
    void resetValidated();

    void setFixedValue(Object value);

    MessageInterpolator getMessageResolver();

    TraversableResolver getTraversableResolver();    

    boolean collectValidated(Object bean, ConstraintValidator constraint);

}
