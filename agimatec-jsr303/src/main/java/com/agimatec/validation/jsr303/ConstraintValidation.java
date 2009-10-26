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
import com.agimatec.validation.jsr303.util.NodeImpl;
import com.agimatec.validation.jsr303.util.PathImpl;
import com.agimatec.validation.jsr303.util.SecureActions;
import com.agimatec.validation.model.Validation;
import com.agimatec.validation.model.ValidationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
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
    private static final Log log = LogFactory.getLog(ConstraintValidation.class);
    private static final String ANNOTATION_PAYLOAD = "payload";
    private static final String ANNOTATION_GROUPS = "groups";

    private final ConstraintValidator[] constraints;
    private final Annotation annotation; // for metadata request API
    private final Field field;
    private final boolean reportFromComposite;
    private final Map<String, Object> attributes;

    private Set<ConstraintValidation> composedConstraints;

    /**
     * the owner is the type where the validation comes from.
     * it is used to support implicit grouping.
     */
    private final Class owner;
    private Set<Class<?>> groups;
    private Set<Class<? extends Payload>> payload;


    /**
     * @param constraints - the constraint validators
     * @param annotation  - the annotation of the constraint
     * @param owner       - the type where the annotated element is placed
     *                    (class, interface, annotation type)
     * @param element     - the annotated element, field, method, class, ...
     */
    protected ConstraintValidation(ConstraintValidator[] constraints,
                                   Annotation annotation, Class owner,
                                   AnnotatedElement element) {
        this.attributes = new HashMap();
        this.constraints = constraints;
        this.annotation = annotation;
        this.owner = owner;
        this.field = element instanceof Field ? (Field) element : null;
        this.reportFromComposite = annotation != null && annotation.annotationType()
              .isAnnotationPresent(ReportAsSingleViolation.class);
        buildFromAnnotation();
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

        if (context.getMetaProperty() != null) {
            if (!isCascadeEnabled(context)) return;
            // compute and cache propertyValue from field
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
                // TODO RSt - how should the composed constraint error report look like?
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

    private boolean isCascadeEnabled(GroupValidationContext context) {
        ElementType etype = field != null ? ElementType.FIELD : ElementType.METHOD;
        PathImpl path = context.getPropertyPath();
        NodeImpl node = path.getLeafNode();
        PathImpl beanPath = path.getPathWithoutLeafNode();
        if (beanPath == null) {
            beanPath = PathImpl.create(null);
        }
        try {
            if (!context.getTraversableResolver()
                  .isReachable(context.getBean(), node,
                        context.getRootMetaBean().getBeanClass(), beanPath, etype))
                return false;
        } catch (RuntimeException e) {
            throw new ValidationException(
                  "Error in TraversableResolver.isReachable() for " + context.getBean(),
                  e);
        }

        try {
            if (!context.getTraversableResolver()
                  .isCascadable(context.getBean(), node,
                        context.getRootMetaBean().getBeanClass(), beanPath, etype))
                return false;
        } catch (RuntimeException e) {
            throw new ValidationException(
                  "Error TraversableResolver.isCascadable() for " + context.getBean(), e);
        }

        return true;
    }

    private void addErrors(GroupValidationContext context,
                           ConstraintValidatorContextImpl jsrContext) {
        context.setConstraintDescriptor(this);
        for (ValidationResults.Error each : jsrContext.getErrorMessages()) {
            // TODO RSt - fix: jsrContexts errors are lost (path info etc...)
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
        /**
         * owner: implicit grouping support:
         * owner is reqGroup or a superclass/superinterface of reqGroup
         */
        return owner.isAssignableFrom(reqGroup) || groups.contains(reqGroup);
    }

    public Class getOwner() {
        return owner;
    }

    /** TODO RSt - generate annotation when descriptor is based on XML */
    public Annotation getAnnotation() {
        return annotation;
    }

    public AnnotatedElement getField() {
        return field;
    }

    /////////////////////////// ConstraintDescriptor implementation


    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /** build attributes, payload, groups from 'annotation' */
    private void buildFromAnnotation() {
        if (annotation != null) {
            SecureActions.run(new PrivilegedAction<Object>() {
                public Object run() {
                    for (Method method : annotation.annotationType()
                          .getDeclaredMethods()) {
                        // enhancement: clarify: should groups + payload also appear in attributes?
                        if (method.getParameterTypes().length == 0) {
                            try {
                                if (ANNOTATION_PAYLOAD.equals(method.getName())) {
                                    buildPayload(method);
                                } else if (ANNOTATION_GROUPS.equals(method.getName())) {
                                    buildGroups(method);
                                } else {
                                    attributes
                                          .put(method.getName(),
                                                method.invoke(annotation));
                                }
                            } catch (Exception e) { // do nothing
                                log.warn("error processing annotation: " + annotation, e);
                            }
                        }
                    }
                    return null;
                }
            });
        }
        try {
            if (groups == null) buildGroups(null);
            if (payload == null) buildPayload(null);
        } catch (Exception e) {
            throw new IllegalArgumentException(e); // execution never reaches this point
        }
    }

    private void buildGroups(Method method)
          throws IllegalAccessException, InvocationTargetException {
        Object raw = method == null ? null : method.invoke(annotation);
        Class<?>[] garr;
        if (raw instanceof Class) {
            garr = new Class<?>[]{(Class) raw};
        } else if (raw instanceof Class<?>[]) {
            garr = (Class<?>[]) raw;
        } else {
            garr = null;
        }
        garr = (garr == null || garr.length == 0) ? GroupsComputer.DEFAULT_GROUP_ARRAY :
              garr;
        this.groups = new HashSet(Arrays.asList(garr));
    }

    private void buildPayload(Method method)
          throws IllegalAccessException, InvocationTargetException {
        Class<Payload>[] payload_raw =
              (Class<Payload>[]) (method == null ? null : method.invoke(annotation));
        if (payload_raw == null) {
            payload = Collections.emptySet();
        } else {
            payload = new HashSet(payload_raw.length);
            payload.addAll(Arrays.asList(payload_raw));
        }
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
        return payload;
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
