package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.MetaBeanManager;

import javax.validation.*;

/**
 * Description: a factory is a complete configurated object that can create validators<br/>
 * this instance is not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 17:06:20 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidatorFactory implements ValidatorFactory, Cloneable, ValidatorBuilder {
    private static AgimatecValidatorFactory DEFAULT_FACTORY;

    private MetaBeanManager metaBeanManager;
    private MessageInterpolator messageResolver;
    private BeanValidator beanValidator;

    /** convenience to retrieve a default global ValidatorFactory */
    public static AgimatecValidatorFactory getDefault() {
        if (DEFAULT_FACTORY == null) {
            AgimatecValidationProvider provider = new AgimatecValidationProvider();
            DEFAULT_FACTORY =
                    provider.buildValidatorFactory(new FactoryBuilderImpl(null, provider));
        }
        return DEFAULT_FACTORY;
    }

    public AgimatecValidatorFactory() {
    }

    public ValidatorBuilder messageInterpolator(MessageInterpolator messageResolver) {
        setMessageInterpolator(messageResolver);
        return this;
    }

    /**
     * * TODO RSt - traversableResolver() nyi
     */
    public ValidatorBuilder traversableResolver(TraversableResolver traversableResolver) {
        return this;
    }

    public Validator getValidator() {
        return new ClassValidator(this);
    }

    /**
     * @return TODO RSt - return separate instance, remove implementation of ValidatorBuilder from here
     */
    public ValidatorBuilder defineValidatorState() {
        return this;
    }

    public Validator getValidator(MessageInterpolator messageResolver) {
        AgimatecValidatorFactory factory = this.clone();
        factory.setMessageInterpolator(messageResolver);
        return new ClassValidator(factory);
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
        return messageResolver;
    }

    public MetaBeanManager getMetaBeanManager() {
        return metaBeanManager;
    }

    public void setBeanValidator(BeanValidator beanValidator) {
        this.beanValidator = beanValidator;
    }

    public BeanValidator getBeanValidator() {
        return beanValidator;
    }
}
