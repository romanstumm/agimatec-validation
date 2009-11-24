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

import javax.validation.*;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;
import java.io.InputStream;
import java.util.*;

/**
 * Description: used to configure agimatec-validation for jsr303.
 * Implementation of Configuration that also implements ConfigurationState,
 * hence this can be passed to buildValidatorFactory(ConfigurationState).
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
    private TraversableResolver traversableResolver;

    // BEGIN Bootstrap parameters for XML
    private Set<InputStream> mappingStreams = new HashSet<InputStream>();
    private Map<String, String> properties = new HashMap<String,String>();
    private boolean ignoreXmlConfiguration = false;
    // END Bootstrap parameters for XML

    public ConfigurationImpl(BootstrapState aState, ValidationProvider aProvider) {
        if (aState != null) {
            this.provider = null;
            if (aState.getValidationProviderResolver() == null) {
                providerResolver = aState.getDefaultValidationProviderResolver();
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
    }

    /**
     * Ignore data from the <i>META-INF/validation.xml</i> file if this
	 * method is called.
     * @return this
     */
    public AgimatecValidatorConfiguration ignoreXmlConfiguration() {
        ignoreXmlConfiguration = true;
        return this;
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
     * Add a stream describing constraint mapping in the Bean Validation
	 * XML format.
     * @return this
     */
    public AgimatecValidatorConfiguration addMapping(InputStream stream) {
        mappingStreams.add(stream);
        return this;
    }

    /**
     * Add a provider specific property. This property is equivalent to
	 * XML configuration properties.
	 * If we do not know how to handle the property, we silently ignore it.
     *
     * @return this
     */
    public AgimatecValidatorConfiguration addProperty(String name, String value) {
        properties.put(name, value);
        return this;
    }

    /**
     * Return a map of non type-safe custom properties.
     *
     * @return null
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Returns true if Configuration.ignoreXMLConfiguration() has been called.
	 * In this case, we ignore META-INF/validation.xml
     * @return true
     */
    public boolean isIgnoreXmlConfiguration() {
        return ignoreXmlConfiguration;
    }

    public Set<InputStream> getMappingStreams() {
        return mappingStreams;
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

    public void setConstraintFactory(ConstraintValidatorFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    public void setMessageInterpolator(MessageInterpolator messageResolver) {
        this.messageInterpolator = messageResolver;
    }
}
