/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.agimatec.validation.jsr303.extensions;

import com.agimatec.validation.jsr303.*;
import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.model.MetaBean;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Description: experimental implementation of method-level-validation <br/>
 * User: roman <br/>
 * Date: 11.11.2009 <br/>
 * Time: 12:36:20 <br/>
 * Copyright: Agimatec GmbH
 */
class MethodValidatorImpl extends ClassValidator implements MethodValidator {
    public MethodValidatorImpl(AgimatecFactoryContext factoryContext) {
        super(factoryContext);
    }

    @Override
    protected BeanDescriptorImpl createBeanDescriptor(MetaBean metaBean) {
        return new MethodBeanDescriptorImpl(factoryContext, metaBean, metaBean.getValidations());
    }    

    /**
     * enhancement: method-level-validation not yet completly implemented
     * <pre>example:
     * <code>
     * public @NotNull String saveItem(@Valid @NotNull Item item, @Max(23) BigDecimal
     * </code></pre>
     * spec:
     * The constraints declarations evaluated are the constraints hosted on the
     * parameters of the method or constructor. If @Valid is placed on a parameter,
     * constraints declared on the object itself are considered.
     *
     * @throws IllegalArgumentException enhancement: if the method does not belong to <code>T</code>
     *                                  or if the Object[] does not match the method signature
     */
    public <T> Set<ConstraintViolation<T>> validateParameters(Class<T> clazz,
                                                              Method method,
                                                              Object[] parameters,
                                                              Class<?>... groupArray) {
        return validateParameters(clazz, method.getParameterAnnotations(), parameters,
              groupArray);
    }

    public <T> Set<ConstraintViolation<T>> validateParameter(Class<T> clazz,
                                                             Method method,
                                                             Object parameter,
                                                             int parameterIndex,
                                                             Class<?>... groupArray) {
        return validateParameter(clazz, method.getParameterAnnotations(), parameter,
              parameterIndex, groupArray);
    }

    public <T> Set<ConstraintViolation<T>> validateParameters(Class<T> clazz,
                                                              Constructor constructor,
                                                              Object[] parameters,
                                                              Class<?>... groupArray) {
        return validateParameters(clazz, constructor.getParameterAnnotations(),
              parameters, groupArray);
    }

    public <T> Set<ConstraintViolation<T>> validateParameter(Class<T> clazz,
                                                             Constructor constructor,
                                                             Object parameter,
                                                             int parameterIndex,
                                                             Class<?>... groupArray) {
        return validateParameter(clazz, constructor.getParameterAnnotations(), parameter,
              parameterIndex, groupArray);
    }

    /**
     * enhancement: validateReturnedValue evaluates the constraints hosted on the method itself.
     * If @Valid  is placed on the method, the constraints declared on the object
     * itself are considered.
     */
    public <T> Set<ConstraintViolation<T>> validateReturnedValue(Class<T> clazz,
                                                                 Method method,
                                                                 Object returnedValue,
                                                                 Class<?>... groupArray) {
        try {
            final GroupValidationContext context =
                  createContext(factoryContext.getMetaBeanFinder()
                        .findForClass(clazz), returnedValue, groupArray);
            final ConstraintValidationListener result = context.getListener();

            // 1. enhancement: validate constraints hosted on the parameters of the method
            // TODO - implement ...

            if (returnedValue != null) {
                // 2. If @Valid is placed on a parameter, validate the parameter itself
                if (method.getAnnotation(Valid.class) != null) {
                    context.setBean(returnedValue,
                          factoryContext.getMetaBeanFinder().
                                findForClass(returnedValue.getClass()));
                    final Groups groups = context.getGroups();
                    // 1. process groups
                    for (Group current : groups.getGroups()) {
                        context.setCurrentGroup(current);
                        validateContext(context);
                    }
                    // 2. process sequences
                    for (List<Group> eachSeq : groups.getSequences()) {
                        for (Group current : eachSeq) {
                            context.setCurrentGroup(current);
                            validateContext(context);
                            /**
                             * if one of the group process in the sequence leads to one or more validation failure,
                             * the groups following in the sequence must not be processed
                             */
                            if (!context.getListener().isEmpty()) break;
                        }
//            if (!context.getListener().isEmpty()) break; // ?? TODO RSt - clarify!
                    }
                }
            }
            return result.getConstaintViolations();
        } catch (RuntimeException ex) {
            throw unrecoverableValidationError(ex, returnedValue);
        }
    }

    private <T> Set<ConstraintViolation<T>> validateParameters(Class<T> clazz,
                                                               Annotation[][] annotations,
                                                               Object[] parameters,
                                                               Class<?>... groupArray) {
        if (parameters == null)
            throw new IllegalArgumentException("cannot validate null");
        if (parameters.length > 0) {
            try {
                final GroupValidationContext context =
                      createContext(factoryContext.getMetaBeanFinder()
                            .findForClass(clazz), parameters, groupArray);
                final ConstraintValidationListener result = context.getListener();

                for (int i = 0; i < parameters.length; i++) {
                    validateParameterInContext(context, annotations, parameters[i], i);
                }
                return result.getConstaintViolations();
            } catch (RuntimeException ex) {
                throw unrecoverableValidationError(ex, parameters);
            }
        } else {
            return Collections.EMPTY_SET;
        }
    }

    private <T> Set<ConstraintViolation<T>> validateParameter(Class<T> clazz,
                                                              Annotation[][] annotations,
                                                              Object parameter,
                                                              int parameterIndex,
                                                              Class<?>... groupArray) {
        try {
            final GroupValidationContext context =
                  createContext(factoryContext.getMetaBeanFinder()
                        .findForClass(clazz), parameter, groupArray);
            final ConstraintValidationListener result = context.getListener();
            validateParameterInContext(context, annotations, parameter, parameterIndex);
            return result.getConstaintViolations();
        } catch (RuntimeException ex) {
            throw unrecoverableValidationError(ex, parameter);
        }
    }

    private void validateParameterInContext(GroupValidationContext context,
                                            Annotation[][] annotations, Object parameter,
                                            int parameterIndex) {

        // 1. enhancement: validate constraints hosted on the parameters of the method
        // TODO - implement ...

        if (parameter != null) {
            for (Annotation anno : annotations[parameterIndex]) {
                // 2. If @Valid is placed on a parameter, validate the parameter itself
                if (anno instanceof Valid) {
                    context.setBean(parameter,
                          factoryContext.getMetaBeanFinder().
                                findForClass(parameter.getClass()));
                    final Groups groups = context.getGroups();
                    // 1. process groups
                    for (Group current : groups.getGroups()) {
                        context.setCurrentGroup(current);
                        validateContext(context);
                    }
                    // 2. process sequences
                    for (List<Group> eachSeq : groups.getSequences()) {
                        for (Group current : eachSeq) {
                            context.setCurrentGroup(current);
                            validateContext(context);
                            /**
                             * if one of the group process in the sequence leads to one or more validation failure,
                             * the groups following in the sequence must not be processed
                             */
                            if (!context.getListener().isEmpty()) break;
                        }
//            if (!context.getListener().isEmpty()) break; // ?? TODO RSt - clarify!
                    }
                    break; // next parameter
                }
            }
        }
    }
}
