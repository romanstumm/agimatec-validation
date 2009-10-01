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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.ValidationException;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.xml.XMLMapper;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:47:44 <br/>
 * Copyright: Agimatec GmbH
 */
public class ConfigurationImpl implements AgimatecValidatorConfiguration, ConfigurationState {
    protected final ValidationProvider provider;
    protected final ValidationProviderResolver providerResolver;
    protected Class<? extends Configuration<?>> providerClass;
    protected MessageInterpolator messageInterpolator, defaultMessageResolver;
    protected ConstraintValidatorFactory constraintFactory;
    private InputStream configurationStream;
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

    public AgimatecValidatorConfiguration beanValidator(BeanValidator beanValidator) {
        setBeanValidator(beanValidator);
        return this;
    }

    public AgimatecValidatorConfiguration configure(InputStream stream) {
        configurationStream = stream;
        if (stream != null) {
            try {
                readValidationXml(stream);
            } catch (Exception e) {
                throw new ValidationException("error reading stream", e);
            }
        }
        return this;
    }

    public InputStream getConfigurationStream() {
        return configurationStream;
    }

    /**
     * TODO RSt - not yet implemented
     *
     * @return false
     */
    public boolean isIgnoreXmlConfiguration() {
        return false;
    }

    public MessageInterpolator getMessageInterpolator() {
        return messageInterpolator;
    }

    public Set<InputStream> getMappingStreams() {
        return null;  // do nothing
    }

    public MessageInterpolator getDefaultMessageInterpolator() {
        return defaultMessageResolver;
    }

    /** main factory method to build a ValidatorFactory
     * TODO RSt - @throw ValidationException if the ValidatorFactory cannot be built
     **/
    public ValidatorFactory buildValidatorFactory() {
        if (provider != null) {
            return provider.buildValidatorFactory(this);
        } else {
            return findProvider().buildValidatorFactory(this);
        }
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return constraintFactory;
    }

    public TraversableResolver getTraversableResolver() {
        return traversableResolver;
    }

    /**
     * TODO RSt - not yet implemented
     *
     * @return null
     */
    public Map<String, String> getProperties() {
        return null;  // do nothing
    }

    public ValidationProvider getProvider() {
        return provider;
    }

    private ValidationProvider findProvider() {
        if (getConfigurationStream() == null) {
            InputStream stream = getClass().getClassLoader()
                  .getResourceAsStream("META-INF/validation.xml");
            if (stream != null) {
                try {
                    readValidationXml(stream);
                    stream.close();
                } catch (Exception e) {
                    throw new ValidationException("error reading stream", e);
                }
            }
        }
        if (providerClass != null) {
            for (ValidationProvider provider : providerResolver
                  .getValidationProviders()) {
                if (provider.isSuitable(providerClass)) {
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

    /**
     * TODO RSt - improve, clarify
     * @param stream
     */
    private void readValidationXml(InputStream stream) {
        XMLMapper.getInstance().getXStream()
              .fromXML(stream, this); 
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
