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

import com.agimatec.validation.MetaBeanBuilder;
import com.agimatec.validation.MetaBeanFactory;
import com.agimatec.validation.MetaBeanManager;
import com.agimatec.validation.jsr303.xml.ValidationMappingMetaBeanFactory;

import javax.validation.Configuration;
import javax.validation.ValidationException;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

/**
 * Description: Implementation of {@link ValidationProvider} for jsr303 implementation of
 * the agimatec-validation framework.
 * <p/>
 * <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:45:41 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidationProvider
      implements ValidationProvider<AgimatecValidatorConfiguration> {
    public boolean isSuitable(Class<? extends Configuration<?>> builderClass) {
        return AgimatecValidatorConfiguration.class == builderClass;
    }

    public ConfigurationImpl createSpecializedConfiguration(
          BootstrapState state) {
        return new ConfigurationImpl(state, this);
    }

    public Configuration<?> createGenericConfiguration(BootstrapState state) {
        return new ConfigurationImpl(state, null);
    }

    /**
     * @throws javax.validation.ValidationException
     *          if the ValidatorFactory cannot be built
     */
    public AgimatecValidatorFactory buildValidatorFactory(ConfigurationState builder) {
        try {
            AgimatecValidatorFactory factory = new AgimatecValidatorFactory();
            /*
             * Create MetaBeanManager that
             * uses JSR303-XML + JSR303-Annotations
             * to build meta-data from
             */
            // this is relevant: xml before annotations
            // (because ignore-annotations settings in xml)
            MetaBeanManager metaBeanManager =
                  new MetaBeanManager(new MetaBeanBuilder(new MetaBeanFactory[]{
                        /*optional: new IntrospectorMetaBeanFactory(),*/
                        /*optional: new XMLMetaBeanFactory(),*/
                        new ValidationMappingMetaBeanFactory(builder),
                        new AnnotationMetaBeanFactory(builder.getConstraintValidatorFactory())}));
            factory.setMetaBeanManager(metaBeanManager);
            factory.setMessageInterpolator(builder.getMessageInterpolator());
            factory.setTraversableResolver(builder.getTraversableResolver());
            factory.setConstraintValidatorFactory(builder.getConstraintValidatorFactory());
            return factory;
        } catch (RuntimeException ex) {
            throw new ValidationException("error building ValidatorFactory", ex);
        }
    }

}
