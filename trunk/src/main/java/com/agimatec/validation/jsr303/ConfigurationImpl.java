package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidator;
import com.agimatec.validation.xml.XMLMapper;

import javax.validation.*;
import javax.validation.bootstrap.DefaultValidationProviderResolver;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 14:47:44 <br/>
 * Copyright: Agimatec GmbH
 */
public class ConfigurationImpl implements AgimatecValidatorConfiguration, ConfigurationState {
    protected final ValidationProvider provider;
    protected final ValidationProviderResolver providerResolver;
    protected Class<? extends Configuration<?>> providerClass;

    protected MessageInterpolator messageResolver, defaultMessageResolver;
    protected ConstraintValidatorFactory constraintFactory;
    private InputStream configurationStream;

    private BeanValidator beanValidator;

    public ConfigurationImpl(BootstrapState aState, ValidationProvider aProvider) {
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
    public AgimatecValidatorConfiguration traversableResolver(TraversableResolver resolver)
    {
        return this;
    }

    private void initializeDefaults() {
        constraintFactory = new DefaultConstraintValidatorFactory();
        messageResolver = new DefaultMessageInterpolator();
        defaultMessageResolver = messageResolver;
        setBeanValidator(new BeanValidator());
    }

    public AgimatecValidatorConfiguration ignoreXmlConfiguration() {
        return this;  // TODO RSt - nyi
    }

    public ConfigurationImpl messageInterpolator(MessageInterpolator resolver) {
        this.messageResolver = resolver;
        return this;
    }

    public ConfigurationImpl constraintValidatorFactory(ConstraintValidatorFactory constraintFactory) {
        setConstraintFactory(constraintFactory);
        return this;
    }

    public AgimatecValidatorConfiguration addMapping(InputStream stream) {
        return this;  // TODO RSt - nyi
    }

    public AgimatecValidatorConfiguration addProperty(String name, String value) {
        return this;  // TODO RSt - nyi
    }

    public AgimatecValidatorConfiguration beanValidator(BeanValidator beanValidator) {
        setBeanValidator(beanValidator);
        return this;
    }

    public AgimatecValidatorConfiguration configure(InputStream stream) {
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

    public boolean isIgnoreXmlConfiguration() {
        return false;  // TODO RSt - nyi
    }

    public MessageInterpolator getMessageInterpolator() {
        return messageResolver;
    }

    public Set<InputStream> getMappingStreams() {
        return null;  // do nothing
    }

    public MessageInterpolator getDefaultMessageInterpolator() {
        return defaultMessageResolver;
    }

    /** main factory method to build a ValidatorFactory */
    public ValidatorFactory buildValidatorFactory() {
        if (provider != null) {
            return provider.buildValidatorFactory(this);
        } else {
            return findProvider().buildValidatorFactory(this);
        }
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return constraintFactory;
    }

    public TraversableResolver getTraversableResolver() {
        return null;  // TODO RSt - getTraversableResolver nyi
    }

    public Map<String, String> getProperties() {
        return null;  // do nothing
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
    public void setProviderClass(Class<? extends Configuration<?>> providerClass) {
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
