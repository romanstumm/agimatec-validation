/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.jsr303.util.NodeImpl;
import com.agimatec.validation.jsr303.util.PathImpl;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.ValidationListener;

import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.IdentityHashMap;

/**
 * Description: instance per validation process, not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 16:32:35 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class GroupValidationContextImpl extends BeanValidationContext
      implements GroupValidationContext, MessageInterpolator.Context {

    private final MessageInterpolator messageResolver;
    private final PathImpl path;
    private final MetaBean rootMetaBean;
    /** the groups in the sequence of validation to take place */
    private Groups groups;
    /** the current group during the validation process */
    private Group currentGroup;

    /**
     * contains the validation constraints that have already been processed during
     * this validation routine (as part of a previous group match)
     */
    private IdentityHashMap<Object, IdentityHashMap<ConstraintValidator, Object>> validatedConstraints =
          new IdentityHashMap();
    private ConstraintDescriptor currentConstraint;
    private TraversableResolver traversableResolver;


    public GroupValidationContextImpl(ValidationListener listener,
                                      MessageInterpolator aMessageResolver,
                                      TraversableResolver traversableResolver,
                                      MetaBean rootMetaBean) {
        super(listener);
        this.messageResolver = aMessageResolver;
        this.traversableResolver = traversableResolver;
        this.rootMetaBean = rootMetaBean;
        this.path = PathImpl.create(null);
    }

    @Override
    public void setCurrentIndex(int index) {
        path.getLeafNode().setIndex(index);
    }

    @Override
    public void setCurrentKey(Object key) {
        path.getLeafNode().setKey(key);
    }

    @Override
    public void moveDown(MetaProperty prop) {
        path.addNode(new NodeImpl(prop.getName()));
        super.moveDown(prop);   // call super!
    }

    @Override
    public void moveUp(Object bean, MetaBean metaBean) {
        path.removeLeafNode();
        super.moveUp(bean, metaBean); // call super!
    }

    /** @return true when the constraint for this object was not already validated in this context */
    public boolean collectValidated(Object bean, ConstraintValidator constraint) {
        IdentityHashMap<ConstraintValidator, Object> beanConstraints =
              validatedConstraints.get(bean);
        if (beanConstraints == null) {
            beanConstraints = new IdentityHashMap();
            validatedConstraints.put(bean, beanConstraints);
        }
        return beanConstraints.put(constraint, Boolean.TRUE) == null;
    }

    public boolean isValidated(Object bean, ConstraintValidator constraint) {
        IdentityHashMap<ConstraintValidator, Object> beanConstraints =
              validatedConstraints.get(bean);
        return beanConstraints != null && beanConstraints.containsKey(constraint);
    }

    public void resetValidatedConstraints() {
        validatedConstraints.clear();
    }

    /**
     * if an associated object is validated,
     * add the association field or JavaBeans property name and a dot ('.') as a prefix
     * to the previous rules.
     * uses prop[index] in property path for elements in to-many-relationships.
     *
     * @return the path in dot notation
     */
    public PathImpl getPropertyPath() {
        PathImpl currentPath = PathImpl.copy(path);
        if (getMetaProperty() != null) {
            currentPath.addNode(new NodeImpl(getMetaProperty().getName()));
        }
        return currentPath;        
    }

    public MetaBean getRootMetaBean() {
        return rootMetaBean;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    public Groups getGroups() {
        return groups;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

    public void setConstraintDescriptor(ConstraintDescriptor constraint) {
        currentConstraint = constraint;
    }

    public ConstraintDescriptor getConstraintDescriptor() {
        return currentConstraint;
    }

    /** @return value being validated */
    public Object getValidatedValue() {
        if (getMetaProperty() != null) {
            return getPropertyValue();
        } else {
            return getBean();
        }
    }

    public MessageInterpolator getMessageResolver() {
        return messageResolver;
    }

    public TraversableResolver getTraversableResolver() {
        return traversableResolver;
    }
}
