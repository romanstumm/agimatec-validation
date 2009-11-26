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

import com.agimatec.validation.IntrospectorMetaBeanFactory;
import com.agimatec.validation.MetaBeanBuilder;
import com.agimatec.validation.MetaBeanFactory;
import com.agimatec.validation.MetaBeanManager;
import com.agimatec.validation.jsr303.util.SecureActions;
import com.agimatec.validation.jsr303.xml.ValidationMappingMetaBeanFactory;
import com.agimatec.validation.xml.XMLMetaBeanFactory;
import org.apache.commons.lang.ClassUtils;

import javax.validation.*;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import javax.validation.spi.ConfigurationState;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: a factory is a complete configurated object that can create validators<br/>
 * this instance is not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 17:06:20 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidatorFactory
      implements ValidatorFactory, Cloneable, AgimatecValidatorConfiguration.Properties {
    private static AgimatecValidatorFactory DEFAULT_FACTORY;

    private MetaBeanManager metaBeanManager;
    private MessageInterpolator messageResolver;
    private TraversableResolver traversableResolver;
    private ConstraintValidatorFactory constraintValidatorFactory;

    /** convenience to retrieve a default global AgimatecValidatorFactory */
    public static AgimatecValidatorFactory getDefault() {
        if (DEFAULT_FACTORY == null) {
            ProviderSpecificBootstrap<AgimatecValidatorConfiguration> provider =
                  Validation.byProvider(AgimatecValidationProvider.class);
            AgimatecValidatorConfiguration configuration = provider.configure();
            DEFAULT_FACTORY = (AgimatecValidatorFactory) configuration
                  .buildValidatorFactory();
        }
        return DEFAULT_FACTORY;
    }

    public AgimatecValidatorFactory(ConfigurationState configuration) {
        setMetaBeanManager(buildMetaBeanManager(configuration));
        setMessageInterpolator(configuration.getMessageInterpolator());
        setTraversableResolver(configuration.getTraversableResolver());
        setConstraintValidatorFactory(configuration.getConstraintValidatorFactory());
    }

    /**
     * Create MetaBeanManager that
     * uses JSR303-XML + JSR303-Annotations
     * to build meta-data from.
     */
    private MetaBeanManager buildMetaBeanManager(ConfigurationState configuration) {
        // this is relevant: xml before annotations
        // (because ignore-annotations settings in xml)
        List<MetaBeanFactory> builders = new ArrayList(4);
        if (Boolean.parseBoolean(configuration.getProperties().get(ENABLE_INTROSPECTOR))) {
            builders.add(new IntrospectorMetaBeanFactory());
        }

        if (!configuration.isIgnoreXmlConfiguration()) {
            builders.add(new ValidationMappingMetaBeanFactory(configuration));
        }

        builders.add(new AnnotationMetaBeanFactory(
              configuration.getConstraintValidatorFactory()));

        if (Boolean.parseBoolean(configuration.getProperties().get(ENABLE_METABEANS_XML))) {
            builders.add(new XMLMetaBeanFactory());
        }
        return new MetaBeanManager(
              new MetaBeanBuilder(builders.toArray(new MetaBeanFactory[builders.size()])));
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

    public final void setMetaBeanManager(MetaBeanManager metaBeanManager) {
        this.metaBeanManager = metaBeanManager;
    }

    public final void setMessageInterpolator(MessageInterpolator messageResolver) {
        this.messageResolver = messageResolver;
    }

    public MessageInterpolator getMessageInterpolator() {
        return ((messageResolver != null) ? messageResolver : getDefaultMessageInterpolator());
    }

    public MetaBeanManager getMetaBeanManager() {
        return metaBeanManager;
    }

    public final void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    public TraversableResolver getTraversableResolver() {
        return traversableResolver;
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return constraintValidatorFactory;
    }

    public final void setConstraintValidatorFactory(
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
