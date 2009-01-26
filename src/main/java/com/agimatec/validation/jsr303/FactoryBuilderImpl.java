package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.xml.XMLMapper;

import javax.validation.*;
import javax.validation.bootstrap.DefaultValidationProviderResolver;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ValidationProvider;
import javax.validation.spi.ValidatorFactoryConfiguration;
import java.io.InputStream;
import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:47:44 <br/>
 * Copyright: Agimatec GmbH
 */
public class FactoryBuilderImpl implements AgimatecValidatorFactoryBuilder, ValidatorFactoryConfiguration {
    protected final ValidationProvider provider;
    protected final ValidationProviderResolver providerResolver;
    protected Class<? extends ValidatorFactoryBuilder<?>> providerClass;

    protected MessageInterpolator messageResolver, defaultMessageResolver;
    protected ConstraintValidatorFactory constraintFactory;
    private InputStream configurationStream;

    private BeanValidator beanValidator;

    public FactoryBuilderImpl(BootstrapState aState, ValidationProvider aProvider) {
        if (aState != null) {
            this.provider = null;
            if (aState.getValidationProviderResolver() == null) {
                providerResolver = new DefaultValidationProviderResolver();
            } else {
                providerResolver = aState.getValidationProviderResolver();
            }
        } else if (aProvider != null) {
            this.provider = aProvider;
            this.providerResolver = null;
        } else {
            throw new ValidationException("either provider or state are required");
        }
        initializeDefaults();
    }

    // TODO RSt - traversableResolver nyi
    public AgimatecValidatorFactoryBuilder traversableResolver(TraversableResolver resolver)
    {
        return this;
    }

    private void initializeDefaults() {
        constraintFactory = new DefaultConstraintValidatorFactory();
        messageResolver = new DefaultMessageInterpolator();
        defaultMessageResolver = messageResolver;
        setBeanValidator(new BeanValidator());
    }

    public FactoryBuilderImpl messageInterpolator(MessageInterpolator resolver) {
        this.messageResolver = resolver;
        return this;
    }

    public FactoryBuilderImpl constraintValidatorFactory(ConstraintValidatorFactory constraintFactory) {
        setConstraintFactory(constraintFactory);
        return this;
    }

    public AgimatecValidatorFactoryBuilder beanValidator(BeanValidator beanValidator) {
        setBeanValidator(beanValidator);
        return this;
    }

    public AgimatecValidatorFactoryBuilder configure(InputStream stream) {
        configurationStream = stream;
        if (stream != null) {
            try {
                readValidationXml(stream);
            } catch (Exception e) {
                throw new ValidationException("error reading stream", e);
            }
        }
        return this;
    }

    public InputStream getConfigurationStream() {
        return configurationStream;
    }

    /** main factory method to build a ValidatorFactory */
    public ValidatorFactory build() {
        if (provider != null) {
            return provider.buildValidatorFactory(this);
        } else {
            return findProvider().buildValidatorFactory(this);
        }
    }

    public MessageInterpolator getMessageInterpolator() {
        return messageResolver;
    }

    public MessageInterpolator getDefaultMessageInterpolator() {
        return defaultMessageResolver;
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return constraintFactory;
    }

    public TraversableResolver getTraversableResolver() {
        return null;  // TODO RSt - getTraversableResolver nyi
    }

    public ValidationProvider getProvider() {
        return provider;
    }

    private ValidationProvider findProvider() {
        if (getConfigurationStream() == null) {
            InputStream stream =
                    getClass().getClassLoader().getResourceAsStream("META-INF/validation.xml");
            if (stream != null) {
                try {
                    readValidationXml(stream);
                    stream.close();
                } catch (Exception e) {
                    throw new ValidationException("error reading stream", e);
                }
            }
        }
        if (providerClass != null) {
            for (ValidationProvider provider : providerResolver.getValidationProviders()) {
                if (provider.isSuitable(providerClass)) {
                    return provider;
                }
            }
            throw new ValidationException("Unable to find suitable provider: " + providerClass);
        } else {
            List<ValidationProvider> providers = providerResolver.getValidationProviders();
            return providers.get(0);
        }
    }

    private void readValidationXml(InputStream stream) {
        XMLMapper.getInstance().getXStream().fromXML(stream, this); // TODO RSt - improve, clarify
    }

    /** used by XStream to set values from configuration file */
    public void setProviderClass(Class<? extends ValidatorFactoryBuilder<?>> providerClass) {
        this.providerClass = providerClass;
    }

    public void setBeanValidator(BeanValidator beanValidator) {
        this.beanValidator = beanValidator;
    }

    public BeanValidator getBeanValidator() {
        return beanValidator;
    }

    public void setConstraintFactory(ConstraintValidatorFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    public void setMessageInterpolator(MessageInterpolator messageResolver) {
        this.messageResolver = messageResolver;
    }
}
