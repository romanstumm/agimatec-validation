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

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.Validator;

import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.jsr303.groups.GroupsComputer;
import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;

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
    protected final AgimatecValidatorFactory factory;
    protected final GroupsComputer groupsComputer = new GroupsComputer();

    public ClassValidator(AgimatecValidatorFactory factory) {
        this.factory = factory;
    }

    /**
     * validate all constraints on object
     * TODO RSt - nyi @throws javax.validation.ValidationException if a non recoverable error happens
     * during the validation process
     */
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(object.getClass());
        GroupValidationContext context = createContext(metaBean, object, groups);
        ConstraintValidationListener result =
              (ConstraintValidationListener) context.getListener();
        Groups sequence = context.getGroups();
        // 1. process groups
        for(Group current : sequence.getGroups()) {
            context.resetValidated();
            context.setCurrentGroup(current);
            factory.getBeanValidator().validateContext(context);
        }
        // 2. process sequences
        for (List<Group> eachSeq : sequence.getSequences()) {
            for (Group current : eachSeq) {
                context.resetValidated();
                context.setCurrentGroup(current);
                factory.getBeanValidator().validateContext(context);
                /**
                 * if one of the group process in the sequence leads to one or more validation failure,
                 * the groups following in the sequence must not be processed
                 */
                if (!result.isEmpty()) break;
            }
        }
        return result.getConstaintViolations();
    }

    /**
     * validate all constraints on <code>propertyName</code> property of object
     *
     * @param propertyName - the attribute name, or nested property name (e.g. prop[2].subpropA.subpropB)
     *                     TODO RSt - nyi @throws javax.validation.ValidationException if a non recoverable error happens
     *                     during the validation process
     */
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName,
                                                            Class<?>... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(object.getClass());
        GroupValidationContext context = createContext(metaBean, object, groups);
        ConstraintValidationListener result =
              (ConstraintValidationListener) context.getListener();
        NestedMetaProperty nestedProp = getNestedProperty(metaBean, object, propertyName);
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
        for(Group current : sequence.getGroups()) {
            context.resetValidated();
            context.setCurrentGroup(current);
            factory.getBeanValidator().validateProperty(context);
        }
        // 2. process sequences
        for (List<Group> eachSeq : sequence.getSequences()) {
            for (Group current : eachSeq) {
                context.resetValidated();
                context.setCurrentGroup(current);
                factory.getBeanValidator().validateProperty(context);
                /**
                 * if one of the group process in the sequence leads to one or more validation failure,
                 * the groups following in the sequence must not be processed
                 */
                if (!result.isEmpty()) break;
            }
        }
        return result.getConstaintViolations();
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
     * TODO RSt - nyi @throws javax.validation.ValidationException if a non recoverable error happens
     * during the validation process
     */
    @SuppressWarnings("unchecked")
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType,
                                                         String propertyName,
                                                         Object value,
                                                         Class<?>... groups) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(beanType);
        GroupValidationContext context = createContext(metaBean, null, groups);
        ConstraintValidationListener<T> result =
              (ConstraintValidationListener<T>) context.getListener();
        context.setMetaProperty(
              getNestedProperty(metaBean, null, propertyName).getMetaProperty());
        context.setFixedValue(value);
        Groups sequence = context.getGroups();
         // 1. process groups
        for(Group current : sequence.getGroups()) {
            context.resetValidated();
            context.setCurrentGroup(current);
            factory.getBeanValidator().validateProperty(context);
        }
        // 2. process sequences
        for (List<Group> eachSeq : sequence.getSequences()) {
            for (Group current : eachSeq) {
                context.resetValidated();
                context.setCurrentGroup(current);
                factory.getBeanValidator().validateProperty(context);
                /**
                 * if one of the group process in the sequence leads to one or more validation failure,
                 * the groups following in the sequence must not be processed
                 */
                if (!result.isEmpty()) break;
            }
        }
        return result.getConstaintViolations();
    }

    protected <T> GroupValidationContext createContext(MetaBean metaBean, T object,
                                                       Class<?>[] groups) {
        ConstraintValidationListener<T> listener =
              new ConstraintValidationListener<T>(object);
        GroupValidationContextImpl context =
              new GroupValidationContextImpl(listener,
                    factory.getMessageInterpolator(),
                    factory.getTraversableResolver(),
                    metaBean);
        context.setBean(object, metaBean);
        context.setGroups(groupsComputer.computeGroups(groups));
        return context;
    }

    /**
     * @param clazz class type evaluated
     * @return true if at least one constraint declaration is present for the given bean
     *         or if one property is marked for validation cascade
     */
    public boolean hasConstraints(Class<?> clazz) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(clazz);
        if (metaBean.getValidations().length > 0) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getValidations().length > 0) return true;
            if (mprop.getMetaBean() != null &&
                  mprop.getFeature(Features.Property.REF_CASCADE, true)) return true;
        }
        return false;
    }

    /**
     * TODO RSt - nyi @throws javax.validation.ValidationException if a non recoverable error happens
     * during the validation process
     */
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(clazz);
        BeanDescriptorImpl edesc =
              metaBean.getFeature(Jsr303Features.Bean.BeanDescriptor);
        if (edesc == null) {
            edesc = new BeanDescriptorImpl(metaBean, metaBean.getValidations());
            metaBean.putFeature(Jsr303Features.Bean.BeanDescriptor, edesc);
        }
        return edesc;
    }

    public AgimatecValidatorFactory getFactory() {
        return factory;
    }

}
