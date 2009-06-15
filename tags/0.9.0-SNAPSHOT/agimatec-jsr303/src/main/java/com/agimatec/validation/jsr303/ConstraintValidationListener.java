package com.agimatec.validation.jsr303;

import com.agimatec.validation.jsr303.groups.GroupsComputer;
import com.agimatec.validation.model.ValidationContext;
import com.agimatec.validation.model.ValidationListener;

import javax.validation.ConstraintDescriptor;
import javax.validation.ConstraintViolation;
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
    private final Set<ConstraintViolation<T>> constaintViolations = new HashSet();
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
        final Set<Class<?>> groups;
        final ConstraintDescriptor constraint;
        if (context instanceof GroupValidationContext) {
            propPath = ((GroupValidationContext) context).getPropertyPath();
            groups = new HashSet(1);
            groups.add(((GroupValidationContext) context).getCurrentGroup().getGroup());
            constraint = ((GroupValidationContext) context).getConstraintDescriptor();
        } else {
            propPath = context.getPropertyName();
            groups = new HashSet(GroupsComputer.DEFAULT_GROUP_ARRAY.length);
            for (Class<?> each : GroupsComputer.DEFAULT_GROUP_ARRAY) {
                groups.add(each);
            }
            constraint = null;
        }
        ConstraintViolationImpl<T> ic = new ConstraintViolationImpl<T>(reason, rootBean,
              context.getBean(), propPath, value, groups, constraint);
        constaintViolations.add(ic);
    }

    public Set<ConstraintViolation<T>> getConstaintViolations() {
        return constaintViolations;
    }

    public boolean isEmpty() {
        return constaintViolations.isEmpty();
    }

    public T getRootBean() {
        return rootBean;
    }
}
