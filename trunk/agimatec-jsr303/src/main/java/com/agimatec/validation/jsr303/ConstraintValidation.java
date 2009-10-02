/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.ValidationResults;
import com.agimatec.validation.jsr303.groups.GroupsComputer;
import com.agimatec.validation.model.Validation;
import com.agimatec.validation.model.ValidationContext;

import javax.validation.ConstraintValidator;
import javax.validation.Path;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
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
public class ConstraintValidation implements Validation, ConstraintDescriptor {
    private final ConstraintValidator[] constraints;
    private final Set<Class<?>> groups;
    private final Annotation annotation; // for metadata request API
    private Set<ConstraintValidation> composedConstraints;
    private final Field field;
    private final boolean reportFromComposite;
    private Map<String, Object> parameters;

    protected ConstraintValidation(ConstraintValidator[] constraints,
                                   Class<?>[] groupsArray, Annotation annotation,
                                   AnnotatedElement element) {
        this.constraints = constraints;

        groupsArray = (groupsArray == null || groupsArray.length == 0) ?
              GroupsComputer.DEFAULT_GROUP_ARRAY : groupsArray;
        this.groups = new HashSet(groupsArray.length);
        this.groups.addAll(Arrays.asList(groupsArray));

        this.annotation = annotation;
        this.field = element instanceof Field ? (Field) element : null;
        this.reportFromComposite = annotation.annotationType()
              .isAnnotationPresent(ReportAsSingleViolation.class);
    }

    public boolean isReportAsSingleViolation() {
        return reportFromComposite;
    }

    public void addComposed(ConstraintValidation aConstraintValidation) {
        if (composedConstraints == null) {
            composedConstraints = new HashSet();
        }
        composedConstraints.add(aConstraintValidation);
    }

    public void validate(ValidationContext context) {
        validate((GroupValidationContext) context);
    }

    public void validate(GroupValidationContext context) {
        /**
         * execute unless the given validation constraint has already been processed
         * during this validation routine (as part of a previous group match)
         */
        if (!isMemberOf(context.getCurrentGroup().getGroup())) {
            return; // do not validate in the current group
        }
        for (ConstraintValidator constraint : constraints) {
            if (!context.collectValidated(context.getBean(), constraint))
                return; // already done
        }


        if (context.getMetaProperty() != null && !context.getTraversableResolver()
              .isCascadable(context.getBean(), (Path.Node) null,
                    // TODO RSt - provide Node
                    context.getRootMetaBean().getBeanClass(), context.getPropertyPath(),
                    field != null ? ElementType.FIELD : ElementType.METHOD)) return;

        if (context.getMetaProperty() !=
              null) { // compute and cache propertyValue from field
            context.getPropertyValue(field);
        }

        // process composed constraints
        if (isReportAsSingleViolation()) {
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
            if (!listener.getConstaintViolations().isEmpty()) {
                // enhancement: how should the composed constraint error report look like?
                ConstraintValidatorContextImpl jsrContext =
                      new ConstraintValidatorContextImpl(context, this);
                addErrors(context, jsrContext); // add defaultErrorMessage only*/
                return;
            }
        } else {
            for (ConstraintValidation composed : getComposed()) {
                composed.validate(context);
            }
        }

        for (ConstraintValidator constraint : constraints) {
            ConstraintValidatorContextImpl jsrContext =
                  new ConstraintValidatorContextImpl(context, this);
            if (!constraint.isValid(context.getValidatedValue(), jsrContext)) {
                addErrors(context, jsrContext);
            }
        }
    }

    private void addErrors(GroupValidationContext context,
                           ConstraintValidatorContextImpl jsrContext) {
        context.setConstraintDescriptor(this);
        for (ValidationResults.Error each : jsrContext.getErrorMessages()) {
            context.getListener().addError(each.getReason(), context);
        }
    }

    public String toString() {
        return "ConstraintValidation{" + Arrays.toString(constraints) + '}';
    }

    public ConstraintValidator[] getConstraintValidators() {
        return constraints;
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

    public Map<String, Object> getAttributes() {
        if (parameters == null) {
            parameters = new HashMap();
            if (getAnnotation() != null) {
                for (Method method : getAnnotation().annotationType()
                      .getDeclaredMethods()) {
                    if (method.getParameterTypes().length == 0) {
                        try {
                            parameters
                                  .put(method.getName(), method.invoke(getAnnotation()));
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

    public Set<Class<? extends Payload>> getPayload() {
        return Collections.EMPTY_SET; // TODO RSt - nyi Payload support
    }

    public List<Class<? extends ConstraintValidator<?, ?>>> getConstraintValidatorClasses() {
        List<Class<? extends ConstraintValidator<?, ?>>> classes =
              new ArrayList(constraints.length);
        for (ConstraintValidator constraint : constraints) {
            classes.add((Class<? extends ConstraintValidator<?, ?>>) constraint
                  .getClass());
        }
        return classes;
    }

}
