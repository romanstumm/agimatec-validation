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

import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.jsr303.groups.GroupsComputer;
import com.agimatec.validation.jsr303.util.SecureActions;
import com.agimatec.validation.model.MetaBean;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import java.util.List;
import java.util.Set;

/**
 * API class -
 * Description:
 * instance is able to validate bean instances (and the associated objects).
 * concurrent, multithreaded access implementation is safe.
 * It is recommended to cache the instance.
 * <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 13:36:33 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ClassValidator implements Validator {
    protected final AgimatecFactoryContext context;
    protected final GroupsComputer groupsComputer = new GroupsComputer();

    public ClassValidator(AgimatecFactoryContext factoryContext) {
        this.context = factoryContext;
    }

    /** @deprecated provided for backward compatibility */
    public ClassValidator(AgimatecValidatorFactory factory) {
        this(factory.usingContext());
    }

    /**
     * validate all constraints on object
     *
     * @throws javax.validation.ValidationException
     *          if a non recoverable error happens during the validation process
     */
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        try {
            MetaBean metaBean = context.getFactory().getMetaBeanManager()
                  .findForClass(object.getClass());
            GroupValidationContext context = createContext(metaBean, object, groups);
            ConstraintValidationListener result =
                  (ConstraintValidationListener) context.getListener();
            Groups sequence = context.getGroups();
            // 1. process groups
            for (Group current : sequence.getGroups()) {
                context.resetValidated();
                context.setCurrentGroup(current);
                this.context.getBeanValidator().validateContext(context);
            }
            // 2. process sequences
            for (List<Group> eachSeq : sequence.getSequences()) {
                for (Group current : eachSeq) {
                    context.resetValidated();
                    context.setCurrentGroup(current);
                    this.context.getBeanValidator().validateContext(context);
                    /**
                     * if one of the group process in the sequence leads to one or more validation failure,
                     * the groups following in the sequence must not be processed
                     */
                    if (!result.isEmpty()) break;
                }
            }
            return result.getConstaintViolations();
        } catch (RuntimeException ex) {
            throw unrecoverableValidationError(ex, object);
        }
    }

    private ValidationException unrecoverableValidationError(RuntimeException ex,
                                                             Object object) {
        throw new ValidationException("error during validation of " + object, ex);
    }

    /**
     * validate all constraints on <code>propertyName</code> property of object
     *
     * @param propertyName - the attribute name, or nested property name (e.g. prop[2].subpropA.subpropB)
     * @throws javax.validation.ValidationException
     *          if a non recoverable error happens
     *          during the validation process
     */
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName,
                                                            Class<?>... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        try {
            MetaBean metaBean =
                  context.getMetaBeanManager().findForClass(object.getClass());
            GroupValidationContext context = createContext(metaBean, object, groups);
            ConstraintValidationListener result =
                  (ConstraintValidationListener) context.getListener();
            NestedMetaProperty nestedProp =
                  getNestedProperty(metaBean, object, propertyName);
            context.setMetaProperty(nestedProp.getMetaProperty());
            if (nestedProp.isNested()) {
                context.setFixedValue(nestedProp.getValue());
            } else {
                context.setMetaProperty(nestedProp.getMetaProperty());
            }
            if (context.getMetaProperty() == null) throw new IllegalArgumentException(
                  "Unknown property " + object.getClass().getName() + "." + propertyName);
            Groups sequence = context.getGroups();
            // 1. process groups
            for (Group current : sequence.getGroups()) {
                context.resetValidated();
                context.setCurrentGroup(current);
                this.context.getBeanValidator().validateProperty(context);
            }
            // 2. process sequences
            for (List<Group> eachSeq : sequence.getSequences()) {
                for (Group current : eachSeq) {
                    context.resetValidated();
                    context.setCurrentGroup(current);
                    this.context.getBeanValidator().validateProperty(context);
                    /**
                     * if one of the group process in the sequence leads to one or more validation failure,
                     * the groups following in the sequence must not be processed
                     */
                    if (!result.isEmpty()) break;
                }
            }
            return result.getConstaintViolations();
        } catch (RuntimeException ex) {
            throw unrecoverableValidationError(ex, object);
        }
    }

    /**
     * find the MetaProperty for the given propertyName,
     * which could contain a path, following the path on a given object to resolve
     * types at runtime from the instance
     */
    private NestedMetaProperty getNestedProperty(MetaBean metaBean, Object t,
                                                 String propertyName) {
        NestedMetaProperty nested = new NestedMetaProperty(propertyName, t);
        nested.setMetaBean(metaBean);
        nested.parse();
        return nested;
    }

    /**
     * validate all constraints on <code>propertyName</code> property
     * if the property value is <code>value</code>
     *
     * @throws javax.validation.ValidationException
     *          if a non recoverable error happens
     *          during the validation process
     */
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType,
                                                         String propertyName,
                                                         Object value,
                                                         Class<?>... groups) {
        try {
            MetaBean metaBean = context.getMetaBeanManager().findForClass(beanType);
            GroupValidationContext context = createContext(metaBean, null, groups);
            ConstraintValidationListener result =
                  (ConstraintValidationListener) context.getListener();
            context.setMetaProperty(
                  getNestedProperty(metaBean, null, propertyName).getMetaProperty());
            context.setFixedValue(value);
            Groups sequence = context.getGroups();
            // 1. process groups
            for (Group current : sequence.getGroups()) {
                context.resetValidated();
                context.setCurrentGroup(current);
                this.context.getBeanValidator().validateProperty(context);
            }
            // 2. process sequences
            for (List<Group> eachSeq : sequence.getSequences()) {
                for (Group current : eachSeq) {
                    context.resetValidated();
                    context.setCurrentGroup(current);
                    this.context.getBeanValidator().validateProperty(context);
                    /**
                     * if one of the group process in the sequence leads to one or more validation failure,
                     * the groups following in the sequence must not be processed
                     */
                    if (!result.isEmpty()) break;
                }
            }
            return result.getConstaintViolations();
        } catch (RuntimeException ex) {
            throw unrecoverableValidationError(ex, beanType);
        }
    }

    protected <T> GroupValidationContext createContext(MetaBean metaBean, T object,
                                                       Class<?>[] groups) {
        ConstraintValidationListener<T> listener =
              new ConstraintValidationListener<T>(object);
        GroupValidationContextImpl context = new GroupValidationContextImpl(listener,
              this.context.getMessageInterpolator(),
              this.context.getTraversableResolver(), metaBean);
        context.setBean(object, metaBean);
        context.setGroups(groupsComputer.computeGroups(groups));
        return context;
    }

    /**
     * @param clazz class type evaluated
     * @return true if at least one constraint declaration is present for the given bean
     *         or if one property is marked for validation cascade
     */
    /*public boolean hasConstraints(Class<?> clazz) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(clazz);
        if (metaBean.getValidations().length > 0) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getValidations().length > 0) return true;
            if (mprop.getMetaBean() != null &&
                  mprop.getFeature(Features.Property.REF_CASCADE, true)) return true;
        }
        return false;
    }*/

    /**
     * Return the descriptor object describing bean constraints
     * The returned object (and associated objects including ConstraintDescriptors)
     * are immutable.
     *
     * @throws ValidationException if a non recoverable error happens
     *                             during the metadata discovery or if some
     *                             constraints are invalid.
     */
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        try {
            MetaBean metaBean = context.getMetaBeanManager().findForClass(clazz);
            BeanDescriptorImpl edesc =
                  metaBean.getFeature(Jsr303Features.Bean.BeanDescriptor);
            if (edesc == null) {
                edesc = new BeanDescriptorImpl(metaBean, metaBean.getValidations());
                metaBean.putFeature(Jsr303Features.Bean.BeanDescriptor, edesc);
            }
            return edesc;
        } catch (RuntimeException ex) {
            throw new ValidationException("error retrieving constraints for " + clazz,
                  ex);
        }
    }

    /**
     * Return an object of the specified type to allow access to the
     * provider-specific API.  If the Bean Validation provider
     * implementation does not support the specified class, the
     * ValidationException is thrown.
     *
     * @param type the class of the object to be returned.
     * @return an instance of the specified class
     * @throws ValidationException if the provider does not
     *                             support the call.
     */
    public <T> T unwrap(Class<T> type) {
        return SecureActions.newInstance(type);
    }
}
