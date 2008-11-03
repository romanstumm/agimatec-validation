package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.ValidationResults;
import com.agimatec.validation.model.Validation;
import com.agimatec.validation.model.ValidationContext;

import javax.validation.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: Adapter between Constraint (JSR303) and Validation (Agimatec)<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 17:31:36 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintValidation implements Validation, ConstraintDescriptor {
    private final Constraint constraint;
    private final Set<String> groups;
    private final Annotation annotation; // for metadata request API
    private Set<ConstraintValidation> composedConstraints;
    private final Field field;
    private boolean reportAsSingleInvalidConstraint;
    private Map<String, Object> parameters;

    protected ConstraintValidation(Constraint constraint, String[] groupsArray,
                                   Annotation annotation, AnnotatedElement element) {
        this.constraint = constraint;

        groupsArray = (groupsArray == null || groupsArray.length == 0) ?
                GroupBeanValidationContext.DEFAULT_GROUPS : groupsArray;
        this.groups = new HashSet(groupsArray.length);
        this.groups.addAll(Arrays.asList(groupsArray));

        this.annotation = annotation;
        this.field = element instanceof Field ? (Field) element : null;
        this.reportAsSingleInvalidConstraint =
                annotation.annotationType().isAnnotationPresent(
                        ReportAsSingleInvalidConstraint.class) ||
                annotation.annotationType().isAnnotationPresent(
                        ReportAsViolationFromComposingConstraint.class);
    }

    /**
     * @deprecated remove when spec is stable
     * @return
     */
    public boolean isReportAsSingleInvalidConstraint() {
        return reportAsSingleInvalidConstraint;
    }

    public boolean ReportAsViolationFromComposingConstraint() {
        return reportAsSingleInvalidConstraint;
    }

    public void addComposed(ConstraintValidation aConstraintValidation) {
        if (composedConstraints == null) {
            composedConstraints = new HashSet();
        }
        composedConstraints.add(aConstraintValidation);
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

        // process composed constraints
        if (isReportAsSingleInvalidConstraint()) {
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
                ContextImpl jsrContext = new ContextImpl(context, this);
                addErrors(context, messageResolver, value, jsrContext); // add defaultErrorMessage only
                return;
            }
        } else {
            for (ConstraintValidation composed : getComposed()) {
                composed.validate(context);
            }
        }

        ContextImpl jsrContext = new ContextImpl(context, this);
        if (!constraint.isValid(value, jsrContext)) {
            addErrors(context, messageResolver, value, jsrContext);
        }
    }

    private void addErrors(ValidationContext context, MessageResolver messageResolver, Object value,
                           ContextImpl jsrContext) {
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

    public Constraint getConstraintImplementation() {
        return constraint;
    }

    protected boolean isMemberOf(String reqGroup) {
        for (String group : groups) {
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

    public Set<String> getGroups() {
        return groups;
    }

    /** @deprecated TODO RSt - remove when interface is fixed */
    public Class<? extends Constraint> getContstraintClass() {
        return getConstraintClass();
    }

    public Class<? extends Constraint> getConstraintClass() {
        return getConstraintImplementation().getClass();
    }
}
