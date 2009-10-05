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

import com.agimatec.validation.jsr303.util.PathImpl;
import com.agimatec.validation.model.ValidationContext;
import com.agimatec.validation.model.ValidationListener;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 14:52:19 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintValidationListener<T> implements ValidationListener {
    private final Set<ConstraintViolation<T>> constaintViolations = new HashSet();
    private final T rootBean;

    public ConstraintValidationListener(T aRootBean) {
        this.rootBean = aRootBean;
    }

    @SuppressWarnings({"ManualArrayToCollectionCopy"})
    public void addError(String reason, ValidationContext context) {
        final Object value;
        if (context.getMetaProperty() == null) value = context.getBean();
        else value = context.getPropertyValue();

        final Path propPath;
        final ConstraintDescriptor constraint;
        String message = reason;
        if (context instanceof GroupValidationContext) {
            GroupValidationContext gcontext = (GroupValidationContext) context;
            if (gcontext instanceof MessageInterpolator.Context) {
                message = gcontext.getMessageResolver()
                      .interpolate(reason, (MessageInterpolator.Context) gcontext);
            } else {
                message = gcontext.getMessageResolver().interpolate(reason, null);
            }
            propPath = gcontext.getPropertyPath();
            constraint = gcontext.getConstraintDescriptor();
        } else {
            propPath = PathImpl.fromString(context.getPropertyName());
            constraint = null;
        }
        ConstraintViolationImpl<T> ic = new ConstraintViolationImpl<T>(reason, message,
              rootBean, context.getBean(), propPath, value, constraint);
        constaintViolations.add(ic);
    }

    public Set<ConstraintViolation<T>> getConstaintViolations() {
        return constaintViolations;
    }

    public boolean isEmpty() {
        return constaintViolations.isEmpty();
    }

    public T getRootBean() {
        return rootBean;
    }
}
