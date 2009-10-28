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

import com.agimatec.validation.jsr303.groups.GroupsComputer;
import com.agimatec.validation.jsr303.util.SecureActions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * Description: helper class that builds a constraintValidation or its
 * composite constraint validations by parsing the jsr303-annotations
 * and providing information (e.g. for @OverridesAttributes) <br/>
 * User: roman <br/>
 * Date: 28.10.2009 <br/>
 * Time: 13:53:13 <br/>
 * Copyright: Agimatec GmbH
 */
final class AnnotationConstraintBuilder {
    private static final Log log = LogFactory.getLog(AnnotationConstraintBuilder.class);
    private static final String ANNOTATION_PAYLOAD = "payload";
    private static final String ANNOTATION_GROUPS = "groups";

    private final ConstraintValidation constraintValidation;
    private List<ConstraintOverrides> overrides;

    public AnnotationConstraintBuilder(ConstraintValidator[] constraintValidators,
                                       Annotation annotation, Class owner,
                                       AnnotatedElement element) {
        boolean reportFromComposite = annotation != null && annotation.annotationType()
              .isAnnotationPresent(ReportAsSingleViolation.class);

        constraintValidation = new ConstraintValidation(constraintValidators, annotation,
              owner, element, reportFromComposite);

        buildFromAnnotation();
    }

    /** build attributes, payload, groups from 'annotation' */
    private void buildFromAnnotation() {
        if (constraintValidation.getAnnotation() != null) {
            SecureActions.run(new PrivilegedAction<Object>() {
                public Object run() {
                    for (Method method : constraintValidation.getAnnotation()
                          .annotationType()
                          .getDeclaredMethods()) {
                        // enhancement: clarify: should groups + payload also appear in attributes?
                        if (method.getParameterTypes().length == 0) {
                            try {
                                if (ANNOTATION_PAYLOAD.equals(method.getName())) {
                                    buildPayload(method);
                                } else if (ANNOTATION_GROUPS.equals(method.getName())) {
                                    buildGroups(method);
                                } else {
                                    constraintValidation.getAttributes()
                                          .put(method.getName(), method.invoke(
                                                constraintValidation.getAnnotation()));
                                }
                            } catch (Exception e) { // do nothing
                                log.warn("error processing annotation: " +
                                      constraintValidation.getAnnotation(), e);
                            }
                        }
                    }
                    return null;
                }
            });
        }
        try {
            if (constraintValidation.getGroups() == null) buildGroups(null);
            if (constraintValidation.getPayload() == null) buildPayload(null);
        } catch (Exception e) {
            throw new IllegalArgumentException(e); // execution never reaches this point
        }
    }

    private void buildGroups(Method method)
          throws IllegalAccessException, InvocationTargetException {
        Object raw =
              method == null ? null : method.invoke(constraintValidation.getAnnotation());
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
        constraintValidation.setGroups(new HashSet(Arrays.asList(garr)));
    }

    private void buildPayload(Method method)
          throws IllegalAccessException, InvocationTargetException {
        Class<Payload>[] payload_raw = (Class<Payload>[]) (method == null ? null :
              method.invoke(constraintValidation.getAnnotation()));
        if (payload_raw == null) {
            constraintValidation
                  .setPayload(Collections.<Class<? extends Payload>>emptySet());
        } else {
            Set pl = new HashSet(payload_raw.length);
            pl.addAll(Arrays.asList(payload_raw));
            constraintValidation.setPayload(pl);
        }
    }

    public ConstraintValidation getConstraintValidation() {
        return constraintValidation;
    }

    /**
     * initialize a child composite 'validation' with @OverridesAttribute
     * from 'constraintValidation' and add to composites.
     */
    public void addComposed(ConstraintValidation composite) {
        applyOverridesAttributes(composite);
        constraintValidation.addComposed(composite); // add AFTER apply()
    }

    private void applyOverridesAttributes(ConstraintValidation composite) {
        if (null == overrides) buildOverridesAttributes();
        if (!overrides.isEmpty()) {
            int index = computeIndex(composite); // assume composite not yet added! (+1)
            if (index < 0) {
                ConstraintOverrides override = // search for constraintIndex = -1
                      findOverride(composite.getAnnotation().annotationType(), -1);
                if (override != null) override.applyOn(composite);
                else override = // search for constraintIndex == 0
                      findOverride(composite.getAnnotation().annotationType(), 0);
                if (override != null) override.applyOn(composite);
            } else { // search for constraintIndex > 0
                ConstraintOverrides override =
                      findOverride(composite.getAnnotation().annotationType(), index + 1);
                if (override != null) override.applyOn(composite);
            }
        }
    }

    private int computeIndex(ConstraintValidation composite) {
        int idx = -1;
        for (ConstraintValidation each : constraintValidation.getComposingValidations()) {
            if (each.getAnnotation().annotationType() ==
                  composite.getAnnotation().annotationType()) {
                idx++;
            }
        }
        return idx;
    }

    /** read overridesAttributes from constraintValidation.annotation */
    private void buildOverridesAttributes() {
        overrides = new LinkedList();
        for (Method method : constraintValidation.getAnnotation()
              .annotationType()
              .getDeclaredMethods()) {
            OverridesAttribute.List annoOAL =
                  method.getAnnotation(OverridesAttribute.List.class);
            if (annoOAL != null) {
                for (OverridesAttribute annoOA : annoOAL.value()) {
                    parseConstraintOverride(method.getName(), annoOA);
                }
            }
            OverridesAttribute annoOA = method.getAnnotation(OverridesAttribute.class);
            if (annoOA != null) {
                parseConstraintOverride(method.getName(), annoOA);
            }
        }
    }

    private void parseConstraintOverride(String methodName, OverridesAttribute oa) {
        ConstraintOverrides target = findOverride(oa.constraint(), oa.constraintIndex());
        if (target == null) {
            target = new ConstraintOverrides(oa.constraint(), oa.constraintIndex());
            overrides.add(target);
        }
        target.values
              .put(oa.name(), constraintValidation.getAttributes().get(methodName));
    }

    private ConstraintOverrides findOverride(Class<? extends Annotation> constraint,
                                             int constraintIndex) {
        for (ConstraintOverrides each : overrides) {
            if (each.constraintType == constraint &&
                  each.constraintIndex == constraintIndex) {
                return each;
            }
        }
        return null;
    }

    /**
     * Holds the values to override in a composed constraint
     * during creation of a composed ConstraintValidation
     */
    private static final class ConstraintOverrides {
        final Class<? extends Annotation> constraintType;
        final int constraintIndex;

        /** key = attributeName, value = overridden value */
        final Map<String, Object> values;

        private ConstraintOverrides(Class<? extends Annotation> constraintType,
                                    int constraintIndex) {
            this.constraintType = constraintType;
            this.constraintIndex = constraintIndex;
            values = new HashMap();
        }

        public void applyOn(ConstraintValidation composite) {
            composite.getAttributes().putAll(values);
        }
    }
}
