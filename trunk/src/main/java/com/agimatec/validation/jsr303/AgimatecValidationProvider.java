package com.agimatec.validation.jsr303;

import com.agimatec.validation.MetaBeanManager;

import javax.validation.ValidationException;
import javax.validation.ValidatorFactoryBuilder;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ValidationProvider;
import javax.validation.spi.ValidatorFactoryConfiguration;

/**
 * Description: Implementation of {@link ValidationProvider} for jsr303 implementation of
 * the agimatec-validation framework.<br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:45:41 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidationProvider implements ValidationProvider {
    public boolean isSuitable(Class<? extends ValidatorFactoryBuilder<?>> builderClass) {
        return AgimatecValidatorFactoryBuilder.class == builderClass;
    }

    public <T extends ValidatorFactoryBuilder<T>> T createSpecializedValidatorFactoryBuilder(
            BootstrapState state, Class<T> builderClass) {
          try {
            return builderClass.cast(new FactoryBuilderImpl(null, this));
        } catch (ClassCastException ex) {
            throw new ValidationException("provider not suitable: " + builderClass, ex);
        }
    }

    public ValidatorFactoryBuilder<?> createGenericValidatorFactoryBuilder(BootstrapState state) {
        return new FactoryBuilderImpl(state, null);
    }

    public AgimatecValidatorFactory buildValidatorFactory(
            ValidatorFactoryConfiguration configuration) {
        FactoryBuilderImpl builder = (FactoryBuilderImpl) configuration;
        AgimatecValidatorFactory factory = new AgimatecValidatorFactory();
        MetaBeanManager metaBeanManager =
                new MetaBeanManager(new AnnotationMetaBeanBuilder(builder.getConstraintFactory()));
        factory.setMetaBeanManager(metaBeanManager);
        factory.setMessageResolver(builder.getMessageResolver());
        factory.setBeanValidator(builder.getBeanValidator());
        return factory;
    }

}
