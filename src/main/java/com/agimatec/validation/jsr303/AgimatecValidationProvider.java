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
 *   * TraversableResolver
 *   * Method-level validation (currently not implemented in JSR303, exists as proposal only)
 *   * JSR-303 XML mappings (currently not yet specified in JSR303),
 *        but proprietary agimatec-validation-XML supported
 *   * group inheritance not yet implemented
 *   * need to improve integration between JSR303 annotations and agimatec-metadata validations. 
 *   * type-safety check when call a constraint-validator implementation's isValid() method
 *   * use of ConstraintViolationException
 *   * javax.validation.constraints.[plural forms] not tested
 *   * difference between ConstraintViolation.getMessageTemplate() and getMessage()
 *   * ValidatorFactory.usingContext()
 * </pre>
 * <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:45:41 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidationProvider implements ValidationProvider {
    public boolean isSuitable(Class<? extends Configuration<?>> builderClass) {
        return AgimatecValidatorConfiguration.class == builderClass;
    }

    public <T extends Configuration<T>> T createSpecializedConfiguration(
            BootstrapState state, Class<T> configurationClass) {
          try {
            return configurationClass.cast(new ConfigurationImpl(null, this));
        } catch (ClassCastException ex) {
            throw new ValidationException("provider not suitable: " + configurationClass, ex);
        }
    }

    public Configuration<?> createGenericConfiguration(BootstrapState state) {
        return new ConfigurationImpl(state, null);
    }

    public AgimatecValidatorFactory buildValidatorFactory(
            ConfigurationState configuration) {
        ConfigurationImpl builder = (ConfigurationImpl) configuration;
        AgimatecValidatorFactory factory = new AgimatecValidatorFactory();
        MetaBeanManager metaBeanManager =
                new MetaBeanManager(new AnnotationMetaBeanBuilder(builder.getConstraintValidatorFactory()));
        factory.setMetaBeanManager(metaBeanManager);
        factory.setMessageInterpolator(builder.getMessageInterpolator());
        factory.setBeanValidator(builder.getBeanValidator());
        return factory;
    }

}
