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

import javax.validation.Configuration;
import javax.validation.ValidationException;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

/**
 * Description: Implementation of {@link ValidationProvider} for jsr303 implementation of
 * the agimatec-validation framework.<pre>
 * TODO RSt - Not yet implemented features:
 *   * @OverridesParameter
 *   * Method-level validation (added in JSR303 1.0.CR3)
 *   * JSR-303 XML mappings (validation.xml), but proprietary agimatec-validation-XML supported
 *   * need to improve integration between JSR303 annotations and agimatec-metadata validations.
 *   * type-safety check when call a constraint-validator implementation's isValid() method
 *   * use of ConstraintViolationException (not clear in spec...?)
 *   * javax.validation.constraints.[plural forms] not tested
 * </pre>
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

    public AgimatecValidatorConfiguration createSpecializedConfiguration(
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
    public AgimatecValidatorFactory buildValidatorFactory(
          ConfigurationState configuration) {
        try {
            ConfigurationImpl builder = (ConfigurationImpl) configuration;
            AgimatecValidatorFactory factory = new AgimatecValidatorFactory();
            MetaBeanManager metaBeanManager = new MetaBeanManager(
                  new AnnotationMetaBeanBuilder(builder.getConstraintValidatorFactory()));
            factory.setMetaBeanManager(metaBeanManager);
            factory.setMessageInterpolator(builder.getMessageInterpolator());
            factory.setTraversableResolver(builder.getTraversableResolver());
            factory.setConstraintValidatorFactory(
                  configuration.getConstraintValidatorFactory());
            factory.setBeanValidator(builder.getBeanValidator());
            return factory;
        } catch (RuntimeException ex) {
            throw new ValidationException("error building ValidatorFactory", ex);
        }
    }

}
