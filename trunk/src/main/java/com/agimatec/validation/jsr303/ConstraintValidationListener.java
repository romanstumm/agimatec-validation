package com.agimatec.validation.jsr303;

import com.agimatec.validation.ValidationContext;
import com.agimatec.validation.ValidationListener;

import javax.validation.InvalidConstraint;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 14:52:19 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintValidationListener<T> implements ValidationListener {
    private final Set<InvalidConstraint<T>> invalidConstraints = new HashSet();
    private final T rootBean;

    public ConstraintValidationListener(T aRootBean) {
        this.rootBean = aRootBean;
    }

    @SuppressWarnings({"ManualArrayToCollectionCopy"})
    public void addError(String reason, ValidationContext context) {
        final Object value;
        if (context.getMetaProperty() == null) value = context.getBean();
        else value = context.getPropertyValue();

        final String propPath;
        final Set<String> groups;
        if(context instanceof GroupBeanValidationContext) {
            propPath = ((GroupBeanValidationContext)context).getPropertyPath();
            groups = new HashSet(1);
            groups.add(((GroupBeanValidationContext)context).getCurrentGroup());
        } else {
            propPath = context.getPropertyName();
            groups = new HashSet(GroupBeanValidationContext.DEFAULT_GROUPS.length);
            for(String each : GroupBeanValidationContext.DEFAULT_GROUPS) {
                groups.add(each);
            }
        }

        InvalidConstraintImpl ic = new InvalidConstraintImpl(reason, rootBean,
                context.getMetaBean().getBeanClass(), propPath, value, groups);
        invalidConstraints.add(ic);
    }

    public Set<InvalidConstraint<T>> getInvalidConstraints() {
        return invalidConstraints;
    }

    public boolean isEmpty() {
        return invalidConstraints.isEmpty();
    }

    public T getRootBean() {
        return rootBean;
    }
}
