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

import com.agimatec.validation.ValidationResults.Error;
import com.agimatec.validation.model.ValidationContext;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: implementation of context provided to  <br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 10:52:27 <br/>
 * Copyright: Agimatec GmbH
 */
class ConstraintContextImpl implements ConstraintValidatorContext {
    protected final ValidationContext vContext;
    protected boolean defaultDisabled;
    protected final List<Error> errors;
    protected final ConstraintValidation validation;

    ConstraintContextImpl(ValidationContext validationContext, ConstraintValidation aConstraintValidation) {
        vContext = validationContext;
        errors = new ArrayList<Error>(5);
        validation = aConstraintValidation;
    }

    public void disableDefaultError() {
        defaultDisabled = true;
    }

    public String getDefaultErrorMessage() {
        return (String) validation.getAttributes().get("message");
    }

    public void addError(String message) {
        /**
         * on property-level use the default property as context information
         */
        errors.add(new Error(message, vContext.getBean(), vContext.getPropertyName()));
    }

    public void addError(String message, String property) {
        /**
         * throw ValidationException when the property is not present
         * on the bean level object
         */
        if (null == vContext.getMetaBean().getProperty(property)) {
            throw new ValidationException(
                    "property {" + property + "} is not present on bean level object");
        }
        errors.add(new Error(message, vContext.getBean(), property));
    }

    protected List<Error> getErrors() {
        if (!defaultDisabled) {
            // use default property on property-level:
            errors.add(new Error(getDefaultErrorMessage(), vContext.getBean(),
                    vContext.getPropertyName()));
            defaultDisabled = false;
        }
        return errors;
    }
}
