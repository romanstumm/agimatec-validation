package com.agimatec.validation.jsr303;

import com.agimatec.validation.MetaBeanManager;

import javax.validation.ValidationException;
import javax.validation.ValidatorBuilder;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ValidationProvider;
import javax.validation.spi.ValidatorBuilderImplementor;

/**
 * Description: Implementation of {@link ValidationProvider} for jsr303 implementation of
 * the agimatec-validation framework.<br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:45:41 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidationProvider implements ValidationProvider {
    public boolean isSuitable(Class<? extends ValidatorBuilder<?>> builderClass) {
        return AgimatecValidatorBuilder.class == builderClass;
    }

    public <T extends ValidatorBuilder<T>> T createSpecializedValidatorBuilder(BootstrapState state,
                                                                               Class<T> builderClass) {
        try {
            return builderClass.cast(new ValidatorBuilderImpl(null, this));
        } catch (ClassCastException ex) {
            throw new ValidationException("provider not suitable: " + builderClass, ex);
        }
    }

    public ValidatorBuilder<?> createGenericValidatorBuilder(BootstrapState state) {
        return new ValidatorBuilderImpl(state, null);
    }

    public AgimatecValidatorFactory buildValidatorFactory(
            ValidatorBuilderImplementor configuration) {
        ValidatorBuilderImpl builder = (ValidatorBuilderImpl) configuration;
        AgimatecValidatorFactory factory = new AgimatecValidatorFactory();
        MetaBeanManager metaBeanManager =
                new MetaBeanManager(new AnnotationMetaBeanBuilder(builder.getConstraintFactory()));
        factory.setMetaBeanManager(metaBeanManager);
        factory.setMessageResolver(builder.getMessageResolver());
        factory.setBeanValidator(builder.getBeanValidator());
        return factory;
    }

}
