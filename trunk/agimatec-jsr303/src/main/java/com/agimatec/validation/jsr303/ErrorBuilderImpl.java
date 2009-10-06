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

import com.agimatec.validation.ValidationResults;
import com.agimatec.validation.jsr303.util.NodeImpl;
import com.agimatec.validation.jsr303.util.PathImpl;

import javax.validation.ConstraintValidatorContext;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 28.09.2009 <br/>
 * Time: 15:28:11 <br/>
 * Copyright: Agimatec GmbH
 */
final class ErrorBuilderImpl implements ConstraintValidatorContext.ErrorBuilder {
    private final ConstraintValidatorContextImpl parent;
    private final String messageTemplate;
    private final PathImpl propertyPath;

    ErrorBuilderImpl(ConstraintValidatorContextImpl contextImpl, String template,
                     PathImpl path) {
        parent = contextImpl;
        messageTemplate = template;
        propertyPath = path;
    }

    public NodeBuilderDefinedContext addSubNode(String name) {
        PathImpl path;
        if (propertyPath.isRootPath()) {
            path = PathImpl.create(name);
        } else {
            path = PathImpl.copy(propertyPath);
            path.addNode(new NodeImpl(name));
        }
        return new NodeBuilderDefinedContextImpl(parent, messageTemplate, path);
    }

    public ConstraintValidatorContext addError() {
        // TODO RSt - fix
        parent.errorMessages.add(new ValidationResults.Error(messageTemplate, null,
              propertyPath.toString()));
        return parent;
    }
}