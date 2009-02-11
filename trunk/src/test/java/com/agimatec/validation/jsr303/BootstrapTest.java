package com.agimatec.validation.jsr303;

import com.agimatec.validation.constraints.NotNullValidator;
import com.agimatec.validation.jsr303.example.Customer;
import junit.framework.TestCase;

import javax.validation.*;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import javax.validation.spi.ValidationProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 29.10.2008 <br/>
 * Time: 15:06:28 <br/>
 * Copyright: Agimatec GmbH
 */
public class BootstrapTest extends TestCase {
    public void testAgimatecBootstrap() {
        Validator validator =
                AgimatecValidatorFactory.getDefault().getValidator();
        assertNotNull(validator);
        assertTrue(AgimatecValidatorFactory.getDefault() ==
                AgimatecValidatorFactory.getDefault());
    }

    public void testEverydayBootstrap() {
        AgimatecValidatorFactory factory =
                (AgimatecValidatorFactory) Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        assertNotNull(validator);

        // each call to Validation.getValidationBuilder() returns a new builder with new state
        AgimatecValidatorFactory factory2 =
                (AgimatecValidatorFactory) Validation.buildDefaultValidatorFactory();
        assertTrue(factory2 != factory);
        assertTrue(factory2.getBeanValidator() != factory.getBeanValidator());
        assertTrue(factory2.getMetaBeanManager() != factory.getMetaBeanManager());
        assertTrue(factory2.getMessageInterpolator() != factory.getMessageInterpolator());

    }

    public void testLocalizedMessageResolverFactory() {
        Configuration<?> builder = Validation.byDefaultProvider().configure();
        // changing the builder allows to create different factories
        DefaultMessageInterpolator messageResolverImpl = new DefaultMessageInterpolator();
        messageResolverImpl.setLocale(Locale.ENGLISH);
        builder.messageInterpolator(messageResolverImpl);
        AgimatecValidatorFactory factory = (AgimatecValidatorFactory) builder.buildValidatorFactory();

        // ALTERNATIVE:
        // you could do it without modifying the builder or reusing it,
        // but then you need to use Agimatec proprietary APIs:
        ((DefaultMessageInterpolator) factory.getMessageInterpolator()).setLocale(Locale.ENGLISH);
        // now factory's message resolver is using the english locale
    }

    /**
     * some tests taken from Hibernate's ValidationTest to ensure that.
     * our implementation works as the reference implementation
     */

    public void testCustomConstraintFactory() {

        Configuration<?> builder = Validation.byDefaultProvider().configure();
        assertDefaultBuilderAndFactory(builder);

        ValidatorFactory factory = builder.buildValidatorFactory();
        Validator validator = factory.getValidator();

        Customer customer = new Customer();
        customer.setFirstName("John");

        Set<ConstraintViolation<Customer>> ConstraintViolations = validator.validate(customer);
        assertFalse(ConstraintViolations.isEmpty());

        builder = Validation.byDefaultProvider().configure();
        builder.constraintValidatorFactory(
                new ConstraintValidatorFactory() {
                    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                        if (key == NotNullValidator.class) {
                            return (T) new BadlyBehavedNotNullValidator();
                        }
                        return new DefaultConstraintValidatorFactory().getInstance(key);
                    }
                }
        );
        factory = builder.buildValidatorFactory();
        validator = factory.getValidator();
        Set<ConstraintViolation<Customer>> ConstraintViolations2 = validator.validate(customer);
        assertTrue("Wrong number of constraints",
                ConstraintViolations.size() > ConstraintViolations2.size());
    }

    public void testCustomResolverAndType() {
        ValidationProviderResolver resolver = new ValidationProviderResolver() {

            public List<ValidationProvider> getValidationProviders() {
                List<ValidationProvider> list = new ArrayList<ValidationProvider>();
                list.add(new AgimatecValidationProvider());
                return list;
            }
        };

        AgimatecValidatorConfiguration builder = Validation
                .byProvider(AgimatecValidatorConfiguration.class)
                .providerResolver(resolver)
                .configure();
        assertDefaultBuilderAndFactory(builder);
    }

    public void testCustomResolver() {
        ValidationProviderResolver resolver = new ValidationProviderResolver() {

            public List<ValidationProvider> getValidationProviders() {
                List<ValidationProvider> list = new ArrayList<ValidationProvider>();
                list.add(new AgimatecValidationProvider());
                return list;
            }
        };

        Configuration<?> builder = Validation
                .byDefaultProvider()
                .providerResolver(resolver)
                .configure();
        assertDefaultBuilderAndFactory(builder);
    }

    private void assertDefaultBuilderAndFactory(Configuration builder) {
        assertNotNull(builder);
        assertTrue(builder instanceof ConfigurationImpl);

        ValidatorFactory factory = builder.buildValidatorFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof AgimatecValidatorFactory);
    }

    public void testFailingCustomResolver() {
        ValidationProviderResolver resolver = new ValidationProviderResolver() {

            public List<ValidationProvider> getValidationProviders() {
                return new ArrayList<ValidationProvider>();
            }
        };

        ProviderSpecificBootstrap<AgimatecValidatorConfiguration> type =
                Validation.byProvider(AgimatecValidatorConfiguration.class);

        final ProviderSpecificBootstrap<AgimatecValidatorConfiguration> specializedBuilderFactory =
                type.providerResolver(resolver);

        try {
            specializedBuilderFactory.configure();
            fail();
        }
        catch (ValidationException e) {
            assertEquals(
                    "Wrong error message",
                    "Unable to find provider: interface " +
                            AgimatecValidatorConfiguration.class.getName(),
                    e.getMessage()
            );
        }
    }

    class BadlyBehavedNotNullValidator extends NotNullValidator {
        @Override
        public boolean isValid(Object object, ConstraintValidatorContext context) {
            return true;
        }
    }
}
