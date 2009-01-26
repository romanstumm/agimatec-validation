package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.ValidationResults;
import com.agimatec.validation.model.Validation;
import com.agimatec.validation.model.ValidationContext;

import javax.validation.ConstraintDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import javax.validation.ReportAsViolationFromCompositeConstraint;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: Adapter between Constraint (JSR303) and Validation (Agimatec)<br/>
 * this instance is immutable!<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 17:31:36 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintValidation implements Validation, ConstraintDescriptor {
    private final ConstraintValidator constraint;
    private final Set<Class<?>> groups;
    private final Annotation annotation; // for metadata request API
    private Set<ConstraintValidation> composedConstraints;
    private final Field field;
    private final boolean reportFromComposite;
    private Map<String, Object> parameters;

    protected ConstraintValidation(ConstraintValidator constraint, Class<?>[] groupsArray,
                                   Annotation annotation, AnnotatedElement element) {
        this.constraint = constraint;

        groupsArray = (groupsArray == null || groupsArray.length == 0) ?
                GroupValidationContext.DEFAULT_GROUPS : groupsArray;
        this.groups = new HashSet(groupsArray.length);
        this.groups.addAll(Arrays.asList(groupsArray));

        this.annotation = annotation;
        this.field = element instanceof Field ? (Field) element : null;
        this.reportFromComposite =
                annotation.annotationType().isAnnotationPresent(
                        ReportAsViolationFromCompositeConstraint.class);
    }

    public boolean isReportAsViolationFromCompositeConstraint() {
        return reportFromComposite;
    }

    public void addComposed(ConstraintValidation aConstraintValidation) {
        if (composedConstraints == null) {
            composedConstraints = new HashSet();
        }
        composedConstraints.add(aConstraintValidation);
    }

    public void validate(ValidationContext context) {
        MessageInterpolator messageResolver = null;
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
            value = context.getPropertyValue(field);
        } else {
            value = context.getBean();
        }

        // process composed constraints
        if (isReportAsViolationFromCompositeConstraint()) {
            BeanValidationContext gctx = (BeanValidationContext) context;
            ConstraintValidationListener oldListener =
                    ((ConstraintValidationListener) gctx.getListener());
            ConstraintValidationListener listener =
                    new ConstraintValidationListener(oldListener.getRootBean());
            gctx.setListener(listener);
            try {
                for (ConstraintValidation composed : getComposed()) {
                    composed.validate(context);
                }
            } finally {
                gctx.setListener(oldListener);
            }
            // stop validating when already failed and ReportAsSingleInvalidConstraint = true ?
            if(!listener.getConstaintViolations().isEmpty()) {
                // enhancement: how should the composed constraint error report look like?
                ConstraintContextImpl jsrContext = new ConstraintContextImpl(context, this);
                addErrors(context, messageResolver, value, jsrContext); // add defaultErrorMessage only
                return;
            }
        } else {
            for (ConstraintValidation composed : getComposed()) {
                composed.validate(context);
            }
        }

        ConstraintContextImpl jsrContext = new ConstraintContextImpl(context, this);
        if (!constraint.isValid(value, jsrContext)) {
            addErrors(context, messageResolver, value, jsrContext);
        }
    }

    private void addErrors(ValidationContext context, MessageInterpolator messageResolver, Object value,
                           ConstraintContextImpl jsrContext) {
        ((GroupValidationContext)context).setCurrentConstraint(this);
        if (messageResolver != null) {
            for (ValidationResults.Error each : jsrContext.getErrors()) {
                context.getListener().addError(
                        messageResolver.interpolate(each.getReason(), this, value),
                        context);
            }
        } else {
            for (ValidationResults.Error each : jsrContext.getErrors()) {
                context.getListener().addError(each.getReason(), context);
            }
        }
    }

    public String toString() {
        return "ConstraintValidation{" + constraint + '}';
    }

    public ConstraintValidator getConstraintValidator() {
        return constraint;
    }

    protected boolean isMemberOf(Class<?> reqGroup) {
        for (Class<?> group : groups) {
            if (group.equals(reqGroup)) return true;
        }
        return false;
    }

    /** TODO RSt - generate annotation when descriptor is based on XML */
    public Annotation getAnnotation() {
        return annotation;
    }

    public AnnotatedElement getField() {
        return field;
    }

    /////////////////////////// ConstraintDescriptor implementation

    /*   public boolean isFieldAccess() {
        return constraintValidation.getField() instanceof Field;
    }*/

    public Map<String, Object> getParameters() {
        if (parameters == null) {
            parameters = new HashMap();
            if (getAnnotation() != null) {
                for (Method method : getAnnotation().annotationType().getDeclaredMethods()) {
                    if (method.getParameterTypes().length == 0) {
                        try {
                            parameters.put(method.getName(), method.invoke(getAnnotation()));
                        } catch (Exception e) { // do nothing
                        }
                    }
                }
            }
        }
        return parameters;
    }

    public Set<ConstraintDescriptor> getComposingConstraints() {
        return composedConstraints == null ? Collections.EMPTY_SET : composedConstraints;
    }

    public Set<ConstraintValidation> getComposed() {
        return composedConstraints == null ? Collections.EMPTY_SET : composedConstraints;
    }

    public Set<Class<?>> getGroups() {
        return groups;
    }

    public Class<? extends ConstraintValidator> getConstraintValidatorClass() {
        return getConstraintValidator().getClass();
    }
}
