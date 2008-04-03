package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.ValidationListener;

import javax.validation.Constraint;
import javax.validation.MessageResolver;
import java.lang.annotation.Annotation;
import java.util.HashSet;

/**
 * Description: Adapter between Constraint (JSR303) and Validation (Agimatec)<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 17:31:36 <br/>
 *
 */
class ConstraintValidation implements Validation {
    private final Constraint constraint;
    private final String[] groups;
    private final String messageKey;
    private final Annotation annotation; // for metadata request API
    private ConstraintDescriptorImpl descriptor;

    public ConstraintValidation(Constraint constraint, String messageKey, String[] groups,
                                Annotation annotation) {
        this.constraint = constraint;
        this.messageKey = messageKey;
        this.groups = (groups == null || groups.length == 0) ?
                GroupValidationContext.DEFAULT_GROUPS : groups;
        this.annotation = annotation;
    }

    public ConstraintDescriptorImpl getConstraintDescriptor() {
        if (descriptor == null) {
            descriptor = new ConstraintDescriptorImpl();
            descriptor.setAnnotation(getAnnotation());
            if (getGroups() != null) {
                descriptor.setGroups(new HashSet(getGroups().length));
                for (String eachGroup : getGroups()) {
                    descriptor.getGroups().add(eachGroup);
                }
            }
            descriptor.setConstraint(getConstraint());
        }
        return descriptor;
    }


    public void validate(ValidationContext context, ValidationListener listener) {
        MessageResolver messageResolver = null;
        /**
         * execute unless the given validation constraint has already been processed
         * during this validation routine (as part of a previous group match)
         */
        if (context instanceof GroupValidationContext) {
            GroupValidationContext groupContext = (GroupValidationContext) context;
            if (!isMemberOf(groupContext.getCurrentGroup())) {
                return; // do not validate in the current group
            }
            if (!groupContext.collectValidated(context.getBean(), constraint))
                return; // already done
            messageResolver = groupContext.getMessageResolver();
        }
        Object value;
        if (context.getMetaProperty() != null) {
            value = context.getPropertyValue();
        } else {
            value = context.getBean();
        }
        if (!constraint.isValid(value)) {
            if (messageResolver != null) {
                listener.addError(
                        messageResolver.interpolate(messageKey, getConstraintDescriptor(), value),
                        context);
            } else {
                listener.addError(messageKey, context);
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
}
