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

import javax.validation.*;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * TODO RSt - split implementation of interface Configuration and ConfigurationState as soon as JSR303-XML configuration is supported
 * <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:47:44 <br/>
 * Copyright: Agimatec GmbH
 */
public class ConfigurationImpl
      implements AgimatecValidatorConfiguration, ConfigurationState {
    protected final ValidationProvider provider;
    protected final ValidationProviderResolver providerResolver;
    protected Class<? extends Configuration<?>> providerClass;
    protected MessageInterpolator messageInterpolator, defaultMessageResolver;
    protected ConstraintValidatorFactory constraintFactory;
    private BeanValidator beanValidator;
    private TraversableResolver traversableResolver;

    public ConfigurationImpl(BootstrapState aState, ValidationProvider aProvider) {
        if (aState != null) {
            this.provider = null;
            if (aState.getValidationProviderResolver() == null) {
                providerResolver = new DefaultValidationProviderResolver();
            } else {
                providerResolver = aState.getValidationProviderResolver();
            }
        } else if (aProvider != null) {
            this.provider = aProvider;
            this.providerResolver = null;
        } else {
            throw new ValidationException("either provider or state are required");
        }
        initializeDefaults();
    }

    public AgimatecValidatorConfiguration traversableResolver(
          TraversableResolver resolver) {
        traversableResolver = resolver;
        return this;
    }

    private void initializeDefaults() {
        constraintFactory = new DefaultConstraintValidatorFactory();
        messageInterpolator = new DefaultMessageInterpolator();
        defaultMessageResolver = messageInterpolator;
        traversableResolver = new DefaultTraversableResolver();
        setBeanValidator(new BeanValidator());
    }

    public AgimatecValidatorConfiguration ignoreXmlConfiguration() {
        return this;  // TODO RSt - nyi
    }

    public ConfigurationImpl messageInterpolator(MessageInterpolator resolver) {
        this.messageInterpolator = resolver;
        return this;
    }

    public ConfigurationImpl constraintValidatorFactory(
          ConstraintValidatorFactory constraintFactory) {
        setConstraintFactory(constraintFactory);
        return this;
    }

    /**
     * TODO RSt - not yet implemented
     *
     * @return this
     */
    public AgimatecValidatorConfiguration addMapping(InputStream stream) {
        return this;
    }

    /**
     * TODO RSt - not yet implemented
     *
     * @return this
     */
    public AgimatecValidatorConfiguration addProperty(String name, String value) {
        return this;
    }

    /**
     * TODO RSt - not yet implemented
     *
     * @return null
     */
    public Map<String, String> getProperties() {
        return null;  // do nothing
    }

    public AgimatecValidatorConfiguration beanValidator(BeanValidator beanValidator) {
        setBeanValidator(beanValidator);
        return this;
    }

    /**
     * TODO RSt - not yet implemented
     *
     * @return true
     */
    public boolean isIgnoreXmlConfiguration() {
        return true;
    }

    // TODO RSt - nyi
    public Set<InputStream> getMappingStreams() {
        return null;  // do nothing
    }

    public MessageInterpolator getMessageInterpolator() {
        return messageInterpolator;
    }

    public MessageInterpolator getDefaultMessageInterpolator() {
        return defaultMessageResolver;
    }

    public TraversableResolver getDefaultTraversableResolver() {
        return traversableResolver;
    }

    // TODO RSt - not used yet
    public ConstraintValidatorFactory getDefaultConstraintValidatorFactory() {
        return constraintFactory;
    }

    /**
     * main factory method to build a ValidatorFactory
     *
     * @throw ValidationException if the ValidatorFactory cannot be built
     */
    public ValidatorFactory buildValidatorFactory() {
        if (provider != null) {
            return provider.buildValidatorFactory(this);
        } else {
            return findProvider().buildValidatorFactory(this);
        }
    }

    // TODO RSt - clarify usage, see AgimatecFactoryContext.getConstraintValidatorFactory()
    //                           and AgimatecValidatorFactory.getConstraintValidatorFactory()
    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return constraintFactory;
    }

    public TraversableResolver getTraversableResolver() {
        return traversableResolver;
    }

    public ValidationProvider getProvider() {
        return provider;
    }

    private ValidationProvider findProvider() {
        /* if (!isIgnoreXmlConfiguration()) {
            InputStream stream = getClass().getClassLoader()
                  .getResourceAsStream("META-INF/validation.xml");
            // TODO RSt - nyi: config by XML
            if (stream != null) {
                try {
                    readValidationXml(stream);
                    stream.close();
                } catch (Exception e) {
                    throw new ValidationException("error reading stream", e);
                }
            }
        }    */
        if (providerClass != null) {
            for (ValidationProvider provider : providerResolver
                  .getValidationProviders()) {
                if (providerClass.isAssignableFrom(provider.getClass())) {
                    return provider;
                }
            }
            throw new ValidationException(
                  "Unable to find suitable provider: " + providerClass);
        } else {
            List<ValidationProvider<?>> providers =
                  providerResolver.getValidationProviders();
            return providers.get(0);
        }
    }

    /** used by XStream to set values from configuration file */
    public void setProviderClass(Class<? extends Configuration<?>> providerClass) {
        this.providerClass = providerClass;
    }

    public void setBeanValidator(BeanValidator beanValidator) {
        this.beanValidator = beanValidator;
    }

    public BeanValidator getBeanValidator() {
        return beanValidator;
    }

    public void setConstraintFactory(ConstraintValidatorFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    public void setMessageInterpolator(MessageInterpolator messageResolver) {
        this.messageInterpolator = messageResolver;
    }
}
