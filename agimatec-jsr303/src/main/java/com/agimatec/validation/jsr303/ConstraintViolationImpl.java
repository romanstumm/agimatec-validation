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

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * Description: Describe a constraint validation defect<br/>
 * From rootBean and propertyPath, it is possible to rebuild the context of the failure
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 14:50:12 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintViolationImpl<T> implements ConstraintViolation<T> {
    private final String messageTemplate;
    private final String message;
    /** root bean validation was invoked on. */
    private final T rootBean;
    private final Class<T> rootBeanClass;
    /** last bean validated. */
    private final Object leafBean;
    private final Object value;
    private final String propertyPath;
    private final Set<Class<?>> groups;
    private final ConstraintDescriptor constraintDescriptor;

    public ConstraintViolationImpl(String messageTemplate, String interpolatedMessage,
                                   Class<T> rootBeanClass, T rootBean,
                                   Object leafBean, Object value,
                                   String propertyPath, ConstraintDescriptor constraintDescriptor) {
        this.messageTemplate = messageTemplate;
        this.message = interpolatedMessage;
        this.rootBeanClass = rootBeanClass;
        this.rootBean = rootBean;
        this.leafBean = leafBean;
        this.value = value;
        this.propertyPath = propertyPath;
        this.groups = new HashSet();
        this.constraintDescriptor = constraintDescriptor;
    }

    public ConstraintViolationImpl(String messageTemplate, String interpolatedMessage,
                                   Class<T> rootBeanClass, T rootBean,
                                   Object leafBean, Object value,
                                   String propertyPath, Set<Class<?>> groups,
                                   ConstraintDescriptor constraintDescriptor) {
        this.messageTemplate = messageTemplate;
        this.message = interpolatedMessage;
        this.rootBeanClass = rootBeanClass;
        this.rootBean = rootBean;
        this.leafBean = leafBean;
        this.value = value;
        this.propertyPath = propertyPath;
        this.groups = groups;
        this.constraintDescriptor = constraintDescriptor;
    }

    /**
     * @deprecated use getMessage() instead
     */
    public String getInterpolatedMessage() {
        return getMessage();
    }

    /**
     * @deprecated use getMessageTemplate() instead
     * @return
     */
    public String getRawMessage() {
        return getMessageTemplate();
    }

    /**
     * former name getInterpolatedMessage()
     * @return The interpolated error message for this constraint violation.
     **/
    public String getMessage() {
        return message;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    /** Root bean being validated validated */
    public T getRootBean() {
        return rootBean;
    }

    public Class<T> getRootBeanClass() {
        return rootBeanClass;
    }

    public Object getLeafBean() {
        return leafBean;
    }

    /** The value failing to pass the constraint */
    public Object getInvalidValue() {
        return value;
    }

    /**
     * the property path to the value from <code>rootBean</code>
     * Null if the value is the rootBean itself
     */
    public String getPropertyPath() {
        return propertyPath;
    }

    /**
     * return the list of groups that the triggered constraint applies on and witch also are
     * within the list of groups requested for validation
     * (directly or through a group sequence)
     */
    public Set<Class<?>> getGroups() {
        return groups;
    }

    public ConstraintDescriptor getConstraintDescriptor() {
        return constraintDescriptor;
    }

    public String toString() {
        return "ConstraintViolationImpl{" + "rootBean=" + rootBean + ", propertyPath='" +
              propertyPath + '\'' + ", message='" + message + '\'' + ", leafBean=" +
              leafBean + ", value=" + value + '}';
    }

}
