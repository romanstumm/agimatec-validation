package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.MetaBeanManager;

import javax.validation.MessageInterpolator;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

/**
 * Description: a factory is a complete configurated object that can create validators<br/>
 * this instance is not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 17:06:20 <br/>
 * Copyright: Agimatec GmbH
 */
public class AgimatecValidatorFactory implements ValidatorFactory, Cloneable {
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

    public MessageInterpolator getDefaultMessageInterpolator() {
        return messageResolver;
    }

    public Validator getValidator() {
        return new ClassValidator(this);
    }

    /**
     * TODO RSt - not yet implemented
     * @return null
     */
    public ValidatorContext usingContext() {
        return null;
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
