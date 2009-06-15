package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.ValidationListener;

import javax.validation.ConstraintDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import java.util.IdentityHashMap;
import java.util.LinkedList;

/**
 * Description: instance per validation process, not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 16:32:35 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class GroupValidationContextImpl extends BeanValidationContext
      implements GroupValidationContext {

    private final MessageInterpolator messageResolver;
    private final LinkedList propertyStack = new LinkedList();
    private Groups groups;
    private Group currentGroup;

    /**
     * contains the validation constraints that have already been processed during
     * this validation routine (as part of a previous group match)
     */
    private IdentityHashMap<Object, IdentityHashMap<ConstraintValidator, Object>> validatedConstraints =
          new IdentityHashMap();
    private ConstraintDescriptor currentConstraint;


    public GroupValidationContextImpl(ValidationListener listener,
                                      MessageInterpolator aMessageResolver) {
        super(listener);
        this.messageResolver = aMessageResolver;
    }

    @Override
    public void setCurrentIndex(int index) {
        super.setCurrentIndex(index);   // call super!
        Object last = propertyStack.getLast();
        if (last instanceof Integer) {
            propertyStack.removeLast();
        }
        propertyStack.addLast(index);
    }

    @Override
    public void moveDown(MetaProperty prop) {
        propertyStack.addLast(prop.getName());
        super.moveDown(prop);   // call super!
    }

    @Override
    public void moveUp(Object bean, MetaBean metaBean) {
        if (propertyStack.removeLast() instanceof Integer) {
            propertyStack.removeLast();
        }
        super.moveUp(bean, metaBean); // call super!
    }

    /** @return true when the constraint for this object was not already validated in this context */
    public boolean collectValidated(Object bean, ConstraintValidator constraint) {
        IdentityHashMap<ConstraintValidator, Object> beanConstraints =
              validatedConstraints.get(bean);
        if (beanConstraints == null) {
            beanConstraints = new IdentityHashMap();
            validatedConstraints.put(bean, beanConstraints);
        }
        return beanConstraints.put(constraint, Boolean.TRUE) == null;
    }

    public boolean isValidated(Object bean, ConstraintValidator constraint) {
        IdentityHashMap<ConstraintValidator, Object> beanConstraints =
              validatedConstraints.get(bean);
        return beanConstraints != null && beanConstraints.containsKey(constraint);
    }

    public void resetValidatedConstraints() {
        validatedConstraints.clear();
    }

    /**
     * if an associated object is validated,
     * add the association field or JavaBeans property name and a dot ('.') as a prefix
     * to the previous rules.
     * uses prop[index] in property path for elements in to-many-relationships.
     *
     * @return the path in dot notation
     */
    public String getPropertyPath() {
        StringBuilder sb = new StringBuilder();
        boolean dot = false;
        for (Object prop : propertyStack) {
            if (prop instanceof String) {
                if (dot) sb.append('.');
                sb.append(prop);
                dot = true;
            } else if (prop instanceof Integer) {
                sb.append('[');
                sb.append(prop);
                sb.append(']');
                dot = true;
            }
        }
        if (getMetaProperty() != null) {
            if (dot) sb.append('.');
            sb.append(getMetaProperty().getName());
        }
        return sb.toString();
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    public Groups getGroups() {
        return groups;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

    public void setConstraintDescriptor(ConstraintDescriptor constraint) {
        currentConstraint = constraint;
    }

    public ConstraintDescriptor getConstraintDescriptor() {
        return currentConstraint;
    }

    /** @return value being validated */
    public Object getValidatedValue() {
        if (getMetaProperty() != null) {
            return getPropertyValue();
        } else {
            return getBean();
        }
    }

    public MessageInterpolator getMessageResolver() {
        return messageResolver;
    }
}
