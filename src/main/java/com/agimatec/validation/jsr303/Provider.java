package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.MetaBeanManager;

import javax.validation.*;
import java.io.InputStream;
import java.util.Locale;

/**
 * Description: This is the main configuration class to customize this
 * JSR303 Provider Implementation<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 14:03:48 <br/>
 * Copyright: Agimatec GmbH 2008
 * TODO RSt - implement bootstrapping APIs
 */
public class Provider implements ValidationProvider, AgimatecValidatorBuilder, ValidatorFactory {
    private static Provider instance;

    protected MetaBeanManager metaBeanManager;
    protected ConstraintFactory constraintFactory;
    private MessageResolver defaultMessageResolver;
    private BeanValidator beanValidator;

    public Provider() {
        initializeDefaults();
    }

    public Provider(ConstraintFactory constraintFactory,
                    MetaBeanManager metaBeanManager) {
        this.constraintFactory = constraintFactory;
        this.metaBeanManager = metaBeanManager;
    }

    public MessageResolver getDefaultMessageResolver() {
        return defaultMessageResolver;
    }

    public MetaBeanManager getMetaBeanManager() {
        return metaBeanManager;
    }

    public void setMetaBeanManager(MetaBeanManager metaBeanManager) {
        this.metaBeanManager = metaBeanManager;
    }

    public ConstraintFactory getConstraintFactory() {
        return constraintFactory;
    }

    /** @deprecated factory method - use getValidator() instead */
    public Validator createValidator(Class aBeanClass) {
        return getValidator(aBeanClass);
    }

    /**
     * Validator factory method - with specific Locale.<br/>
     * Alternatively you can change the default message resolver (and its locale)
     * by calling com.agimatec.validation.jsr303.Provider.setDefaultMessageResolver()
     * @deprecated will be replaced by bootstrapping solution
     */
    public Validator createValidator(Class aBeanClass, Locale locale) {
        Validator validator = getValidator(aBeanClass);
        MessageResolverImpl messageResolver = new MessageResolverImpl();
        messageResolver.setLocale(locale);
        // TODO RSt - change for bootstrapping
        ((ClassValidator) validator).setMessageResolver(messageResolver);
        return validator;
    }

    /**
     * factory method -
     *
     * @see javax.validation.ValidationProviderFactory#createValidator(Class)
     */
    public Validator createValidator(String metaBeanId) {
        return new ClassValidator(this, getMetaBeanManager().findForId(metaBeanId));
    }

    /**
     * factory method -
     * @returns an initialized Validator instance for the specific class.
     * Validator instances can be pooled and shared by the implementation
     * In this scenario, the implementation must return thread-safe Validator implementations
     */
    public <T> Validator<T> getValidator(Class<T> aBeanClass) {
        return new ClassValidator(this, getMetaBeanManager().findForClass(aBeanClass));
    }

    public BeanValidator getBeanValidator() {
        return beanValidator;
    }

    public void setBeanValidator(BeanValidator beanValidator) {
        this.beanValidator = beanValidator;
    }

    public static Provider getInstance() {
        if (instance == null) {
            ValidationProvider provider = ValidationProviderFactory.getProvider();
            if (provider instanceof Provider) {
                instance = (Provider) provider;
            } else {
                instance = new Provider();
            }
        }
        return instance;
    }

    public void setInstance(Provider aProvider) {
        instance = aProvider;
    }

    private void initializeDefaults() {
        setMetaBeanManager(new MetaBeanManager(new AnnotationMetaBeanBuilder(this)));
        constraintFactory(new DefaultConstraintFactory());
        messageResolver(new MessageResolverImpl());
        setBeanValidator(new BeanValidator());
    }

    public Provider messageResolver(MessageResolver resolver) {
        this.defaultMessageResolver = resolver;
        return this;
    }

    public AgimatecValidatorBuilder constraintFactory(ConstraintFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
        return this;
    }

    public AgimatecValidatorBuilder configure(InputStream stream) {
        return null;  // TODO RSt - nyi
    }

    public ValidatorFactory build() {
        return this;  // TODO RSt - nyi correctly
    }

}
