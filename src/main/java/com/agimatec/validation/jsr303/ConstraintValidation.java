package com.agimatec.validation.jsr303;

import com.agimatec.validation.Validation;
import com.agimatec.validation.ValidationContext;

import javax.validation.Constraint;
import javax.validation.MessageResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

/**
 * Description: Adapter between Constraint (JSR303) and Validation (Agimatec)<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 17:31:36 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintValidation implements Validation {
    private final Constraint constraint;
    private final String[] groups;
    private final String messageKey;
    private final Annotation annotation; // for metadata request API
    private ConstraintDescriptorImpl descriptor;
    private final Field field;

    protected ConstraintValidation(Constraint constraint, String messageKey, String[] groups,
                                Annotation annotation, AnnotatedElement element) {
        this.constraint = constraint;
        this.messageKey = messageKey;
        this.groups = (groups == null || groups.length == 0) ?
                GroupBeanValidationContext.DEFAULT_GROUPS : groups;
        this.annotation = annotation;
        this.field = element instanceof Field ? (Field)element : null;
    }

    public ConstraintDescriptorImpl getConstraintDescriptor() {
        if (descriptor == null) {
            descriptor = new ConstraintDescriptorImpl(this);
        }
        return descriptor;
    }

    public void validate(ValidationContext context) {
        MessageResolver messageResolver = null;
        /**
         * execute unless the given validation constraint has already been processed
         * during this validation routine (as part of a previous group match)
         */
        if (context instanceof GroupBeanValidationContext) {
            GroupBeanValidationContext groupContext = (GroupBeanValidationContext) context;
            if (!isMemberOf(groupContext.getCurrentGroup())) {
                return; // do not validate in the current group
            }
            if (!groupContext.collectValidated(context.getBean(), constraint))
                return; // already done
            messageResolver = groupContext.getMessageResolver();

        }
        Object value;
        if (context.getMetaProperty() != null) {
            value = context.getPropertyValue(field);
        } else {
            value = context.getBean();
        }
        // TODO RSt - provide context
        if (!constraint.isValid(value, null)) {
            if (messageResolver != null) {
                context.getListener().addError(
                        messageResolver.interpolate(messageKey, getConstraintDescriptor(), value),
                        context);
            } else {
                context.getListener().addError(messageKey, context);
            }
        }
    }

    public String toString() {
        return "ConstraintValidation{" + constraint + '}';
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public String[] getGroups() {
        return groups;
    }

    protected boolean isMemberOf(String reqGroup) {
        for (String group : groups) {
            if (group.equals(reqGroup)) return true;
        }
        return false;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public AnnotatedElement getField() {
        return field;
    }
}
