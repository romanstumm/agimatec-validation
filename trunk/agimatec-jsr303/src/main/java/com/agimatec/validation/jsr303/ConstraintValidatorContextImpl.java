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

import com.agimatec.validation.model.ValidationListener;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Path;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 28.09.2009 <br/>
 * Time: 14:19:30 <br/>
 * Copyright: Agimatec GmbH
 */
public class ConstraintValidatorContextImpl implements ConstraintValidatorContext {
    private static final String ANNOTATION_MESSAGE = "message";
    private final List<ValidationListener.Error> errorMessages =
          new LinkedList<ValidationListener.Error>();

    private final ConstraintValidation constraintDescriptor;
    private final GroupValidationContext validationContext;

    private boolean defaultDisabled;

    public ConstraintValidatorContextImpl(GroupValidationContext validationContext,
                                          ConstraintValidation aConstraintValidation) {
        this.validationContext = validationContext;
        this.constraintDescriptor = aConstraintValidation;
    }

    public void disableDefaultConstraintViolation() {
        defaultDisabled = true;
    }

    public String getDefaultConstraintMessageTemplate() {
        return (String) constraintDescriptor.getAttributes().get(ANNOTATION_MESSAGE);
    }

    public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String messageTemplate) {
        return new ConstraintViolationBuilderImpl(this, messageTemplate,
              validationContext.getPropertyPath());
    }

    public List<ValidationListener.Error> getErrorMessages() {
        if (defaultDisabled && errorMessages.isEmpty()) {
            throw new ValidationException(
                  "At least one custom message must be created if the default error message gets disabled.");
        }

        List<ValidationListener.Error> returnedErrorMessages =
              new ArrayList<ValidationListener.Error>(errorMessages);
        if (!defaultDisabled) {
            returnedErrorMessages.add(new ValidationListener.Error(
                  getDefaultConstraintMessageTemplate(), validationContext.getPropertyPath(), null));
        }
        return returnedErrorMessages;
    }

    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return constraintDescriptor;
    }

    public GroupValidationContext getValidationContext() {
        return validationContext;
    }

    public void addError(String messageTemplate, Path propertyPath) {
        errorMessages.add(new ValidationListener.Error(messageTemplate,
              propertyPath, null));
    }
}
