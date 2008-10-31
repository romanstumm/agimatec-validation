package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.BeanValidator;
import com.agimatec.utility.validation.MetaBeanManager;

import javax.validation.*;
import java.util.Locale;

/**
 * Description: This is the main configuration class to customize this
 * JSR303 Provider Implementation<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 14:03:48 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class Provider implements ValidationProvider {
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

    public void setDefaultMessageResolver(MessageResolver defaultMessageResolver) {
        this.defaultMessageResolver = defaultMessageResolver;
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

    public void setConstraintFactory(ConstraintFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    /**
     * factory method -
     * @param aBeanClass
     * @return
     */
    public Validator createValidator(Class aBeanClass) {
        return new ClassValidator(this, getMetaBeanManager().findForClass(aBeanClass));
    }

    /**
     * Validator factory method - with specific Locale.<br/>
     * Alternatively you can change the default message resolver (and its locale)
     * by calling com.agimatec.utility.validation.jsr303.Provider.setDefaultMessageResolver()
     * @param aBeanClass
     * @param locale
     * @return
     */
    public Validator createValidator(Class aBeanClass, Locale locale) {
        Validator validator = createValidator(aBeanClass);
        MessageResolverImpl messageResolver = new MessageResolverImpl();
        messageResolver.setLocale(locale);
        validator.setMessageResolver(messageResolver);
        return validator;
    }

    /**
     * factory method -
     * @param metaBeanId
     * @see javax.validation.ValidationProviderFactory#createValidator(Class)
     * @return
     */
    public Validator createValidator(String metaBeanId) {
        return new ClassValidator(this, getMetaBeanManager().findForId(metaBeanId));
    }

    public BeanValidator getBeanValidator() {
        return beanValidator;
    }

    public void setBeanValidator(BeanValidator beanValidator) {
        this.beanValidator = beanValidator;
    }

    public static Provider getInstance() {
        if(instance == null) {
            ValidationProvider provider = ValidationProviderFactory.getProvider();
            if(provider instanceof Provider) {
                instance = (Provider)provider;
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
        setConstraintFactory(new DefaultConstraintFactory());
        setDefaultMessageResolver(new MessageResolverImpl());
        setBeanValidator(new BeanValidator());
    }

}
