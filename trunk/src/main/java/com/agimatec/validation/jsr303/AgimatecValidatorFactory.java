package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.MetaBeanManager;

import javax.validation.*;
import java.io.InputStream;

/**
 * Description: a factory is a complete configurated object that can create validators<br/>
 * this instance is not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 17:06:20 <br/>
 * Copyright: Agimatec GmbH
 */                                                                         /* TODO RSt - unklar? */
public class AgimatecValidatorFactory implements ValidatorFactory, Cloneable, Configuration {
    private static AgimatecValidatorFactory DEFAULT_FACTORY;

    private MetaBeanManager metaBeanManager;
    private MessageInterpolator messageResolver;
    private BeanValidator beanValidator;

    /** convenience to retrieve a default global ValidatorFactory */
    public static AgimatecValidatorFactory getDefault() {
        if (DEFAULT_FACTORY == null) {
            AgimatecValidationProvider provider = new AgimatecValidationProvider();
            DEFAULT_FACTORY =
                    provider.buildValidatorFactory(new ConfigurationImpl(null, provider));
        }
        return DEFAULT_FACTORY;
    }

    public AgimatecValidatorFactory() {
    }

    public Configuration ignoreXmlConfiguration() {
        return null;  // do nothing
    }

    public Configuration messageInterpolator(MessageInterpolator messageResolver) {
        setMessageInterpolator(messageResolver);
        return this;
    }

    /**
     * * TODO RSt - traversableResolver() nyi
     */
    public Configuration traversableResolver(TraversableResolver traversableResolver) {
        return this;
    }

    public Configuration constraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        return this;  // TODO RSt - nyi
    }

    public Configuration addMapping(InputStream stream) {
        return null;  // TODO RSt - nyi
    }

    public Configuration addProperty(String name, String value) {
        return null;  // TODO RSt - nyi
    }

    public MessageInterpolator getDefaultMessageInterpolator() {
        return messageResolver;
    }

    public ValidatorFactory buildValidatorFactory() {
        return this;     // TODO RSt - nyi
    }

    public Validator getValidator() {
        return new ClassValidator(this);
    }

    public ValidatorContext usingContext() {
        return null;  // TODO RSt - nyi
    }

    /**
     * @return TODO RSt - return separate instance, remove implementation of ValidatorBuilder from here
     */
    public Configuration defineValidatorState() {
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
