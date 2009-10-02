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

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.MetaBeanFinder;

import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;

/**
 * Description: Represents the context that is used to create <code>ClassValidator</code>
 * instances.<br/>
 * User: roman <br/>
 * Date: 01.10.2009 <br/>
 * Time: 16:35:25 <br/>
 * Copyright: Agimatec GmbH
 */
class AgimatecFactoryContext implements ValidatorContext {
    private MessageInterpolator messageInterpolator;
    private TraversableResolver traversableResolver;
    private AgimatecValidatorFactory factory;

    public AgimatecFactoryContext(AgimatecValidatorFactory factory) {
        this.factory = factory;
    }

    public AgimatecValidatorFactory getFactory() {
        return factory;
    }

    public ValidatorContext messageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
        return this;
    }

    public ValidatorContext traversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
        return this;
    }

    public Validator getValidator() {
        return new ClassValidator(this);
    }

    public BeanValidator getBeanValidator() {
        return factory.getBeanValidator();
    }

    public MetaBeanFinder getMetaBeanManager() {
        return factory.getMetaBeanManager();
    }

    public MessageInterpolator getMessageInterpolator() {
        return messageInterpolator == null ? factory.getMessageInterpolator() :
              messageInterpolator;
    }

    public TraversableResolver getTraversableResolver() {
        return traversableResolver == null ? factory.getTraversableResolver() :
              traversableResolver;
    }
}