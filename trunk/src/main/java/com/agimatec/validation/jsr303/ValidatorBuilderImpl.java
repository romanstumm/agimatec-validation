package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.xml.XMLMapper;

import javax.validation.*;
import javax.validation.bootstrap.DefaultValidationProviderResolver;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ValidationProvider;
import javax.validation.spi.ValidatorBuilderImplementor;
import java.io.InputStream;
import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:47:44 <br/>
 * Copyright: Agimatec GmbH
 */
public class ValidatorBuilderImpl implements AgimatecValidatorBuilder, ValidatorBuilderImplementor {
    protected final ValidationProvider provider;
    protected final ValidationProviderResolver providerResolver;
    protected Class<? extends ValidatorBuilder<?>> providerClass;

    protected MessageResolver messageResolver;
    protected ConstraintFactory constraintFactory;
    private InputStream configurationStream;

    private BeanValidator beanValidator;

    public ValidatorBuilderImpl(BootstrapState aState, ValidationProvider aProvider) {
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

    private void initializeDefaults() {
        constraintFactory = new DefaultConstraintFactory();
        messageResolver = new DefaultMessageResolver();
        setBeanValidator(new BeanValidator());
    }

    public ValidatorBuilderImpl messageResolver(MessageResolver resolver) {
        this.messageResolver = resolver;
        return this;
    }

    public ValidatorBuilderImpl constraintFactory(ConstraintFactory constraintFactory) {
        setConstraintFactory(constraintFactory);
        return this;
    }

    public AgimatecValidatorBuilder beanValidator(BeanValidator beanValidator) {
        setBeanValidator(beanValidator);
        return this;
    }

    public AgimatecValidatorBuilder configure(InputStream stream) {
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

    public MessageResolver getMessageResolver() {
        return messageResolver;
    }

    public ConstraintFactory getConstraintFactory() {
        return constraintFactory;
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
    public void setProviderClass(Class<? extends ValidatorBuilder<?>> providerClass) {
        this.providerClass = providerClass;
    }

    public void setBeanValidator(BeanValidator beanValidator) {
        this.beanValidator = beanValidator;
    }

    public BeanValidator getBeanValidator() {
        return beanValidator;
    }

    public void setConstraintFactory(ConstraintFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    public void setMessageResolver(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }
}
