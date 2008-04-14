package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.ValidationListener;

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

    public void addError(String reason, ValidationContext context) {
        final Object value;
        if (context.getMetaProperty() == null) value = context.getBean();
        else value = context.getPropertyValue();

        final String propPath;
        final String[] groups;
        if(context instanceof GroupValidationContext) {
            propPath = ((GroupValidationContext)context).getPropertyPath();
            groups = new String[] { ((GroupValidationContext)context).getCurrentGroup()};
        } else {
            propPath = context.getPropertyName();
            groups = GroupValidationContext.DEFAULT_GROUPS;
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
