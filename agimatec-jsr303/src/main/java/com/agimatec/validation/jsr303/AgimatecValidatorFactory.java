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

import com.agimatec.validation.MetaBeanManager;
import com.agimatec.validation.jsr303.util.SecureActions;
import org.apache.commons.lang.ClassUtils;

import javax.validation.*;

/**
 * Description: a factory is a complete configurated object that can create validators<br/>
 * this instance is not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 17:06:20 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidatorFactory implements ValidatorFactory, Cloneable {
    private static AgimatecValidatorFactory DEFAULT_FACTORY;

    private MetaBeanManager metaBeanManager;
    private MessageInterpolator messageResolver;
    private TraversableResolver traversableResolver;
    private ConstraintValidatorFactory constraintValidatorFactory;

    /** convenience to retrieve a default global ValidatorFactory */
    public static AgimatecValidatorFactory getDefault() {
        if (DEFAULT_FACTORY == null) {
            AgimatecValidationProvider provider = new AgimatecValidationProvider();
            DEFAULT_FACTORY =
                  provider.buildValidatorFactory(new ConfigurationImpl(null, provider));
        }
        return DEFAULT_FACTORY;
    }

    public AgimatecValidatorFactory() {
    }

    protected MessageInterpolator getDefaultMessageInterpolator() {
        return messageResolver;
    }

    /**
     * shortcut method to create a new Validator instance with factory's settings
     *
     * @return the new validator instance
     */
    public Validator getValidator() {
        return usingContext().getValidator();
    }

    /** @return the validator factory's context */
    public AgimatecFactoryContext usingContext() {
        return new AgimatecFactoryContext(this);
    }

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public synchronized AgimatecValidatorFactory clone() {
        try {
            return (AgimatecValidatorFactory) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(); // VM bug.
        }
    }

    public void setMetaBeanManager(MetaBeanManager metaBeanManager) {
        this.metaBeanManager = metaBeanManager;
    }

    public void setMessageInterpolator(MessageInterpolator messageResolver) {
        this.messageResolver = messageResolver;
    }

    public MessageInterpolator getMessageInterpolator() {
        return ((messageResolver != null) ? messageResolver :
              getDefaultMessageInterpolator());
    }

    public MetaBeanManager getMetaBeanManager() {
        return metaBeanManager;
    }

    public void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    public TraversableResolver getTraversableResolver() {
        return traversableResolver;
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return constraintValidatorFactory;
    }

    public void setConstraintValidatorFactory(
          ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
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
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        } else if (!type.isInterface()) {
            return SecureActions.newInstance(type);
        } else {
            try {
                Class<T> cls = ClassUtils.getClass(type.getName() + "Impl");
                return SecureActions.newInstance(cls);
            } catch (ClassNotFoundException e) {
                throw new ValidationException("Type " + type + " not supported");
            }
        }
    }
}
