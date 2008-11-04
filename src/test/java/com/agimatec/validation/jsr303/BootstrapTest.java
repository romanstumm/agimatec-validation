package com.agimatec.validation.jsr303;

import com.agimatec.validation.constraints.NotNullConstraint;
import com.agimatec.validation.jsr303.example.Address;
import com.agimatec.validation.jsr303.example.Customer;
import junit.framework.TestCase;

import javax.validation.*;
import javax.validation.bootstrap.SpecializedBuilderFactory;
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
        Validator<Address> validator =
                AgimatecValidatorFactory.getDefault().getValidator(Address.class);
        assertNotNull(validator);
        assertTrue(AgimatecValidatorFactory.getDefault() ==
                AgimatecValidatorFactory.getDefault());
    }

    public void testEverydayBootstrap() {
        AgimatecValidatorFactory factory =
                (AgimatecValidatorFactory) Validation.getBuilder().build();
        Validator<Address> validator = factory.getValidator(Address.class);
        assertNotNull(validator);

        // each call to Validation.getValidationBuilder() returns a new builder with new state
        AgimatecValidatorFactory factory2 =
                (AgimatecValidatorFactory) Validation.getBuilder().build();
        assertTrue(factory2 != factory);
        assertTrue(factory2.getBeanValidator() != factory.getBeanValidator());
        assertTrue(factory2.getMetaBeanManager() != factory.getMetaBeanManager());
        assertTrue(factory2.getMessageResolver() != factory.getMessageResolver());

    }

    public void testLocalizedMessageResolverFactory() {
        ValidatorFactoryBuilder<?> builder = Validation.getBuilder();
        // changing the builder allows to create different factories
        DefaultMessageResolver messageResolverImpl = new DefaultMessageResolver();
        messageResolverImpl.setLocale(Locale.ENGLISH);
        builder.messageResolver(messageResolverImpl);
        AgimatecValidatorFactory factory = (AgimatecValidatorFactory) builder.build();

        // ALTERNATIVE:
        // you could do it without modifying the builder or reusing it,
        // but then you need to use Agimatec proprietary APIs:
        ((DefaultMessageResolver) factory.getMessageResolver()).setLocale(Locale.ENGLISH);
        // now factory's message resolver is using the english locale
    }

    /**
     * some tests taken from Hibernate's ValidationTest to ensure that.
     * our implementation works as the reference implementation
     */

    public void testCustomConstraintFactory() {

        ValidatorFactoryBuilder<?> builder = Validation.getBuilder();
        assertDefaultBuilderAndFactory(builder);

        ValidatorFactory factory = builder.build();
        Validator<Customer> validator = factory.getValidator(Customer.class);

        Customer customer = new Customer();
        customer.setFirstName("John");

        Set<ConstraintViolation<Customer>> ConstraintViolations = validator.validate(customer);
        assertFalse(ConstraintViolations.isEmpty());

        builder = Validation.getBuilder();
        builder.constraintFactory(
                new ConstraintFactory() {
                    public <T extends Constraint> T getInstance(Class<T> key) {
                        if (key == NotNullConstraint.class) {
                            return (T) new BadlyBehavedNotNullConstraint();
                        }
                        return new DefaultConstraintFactory().getInstance(key);
                    }
                }
        );
        factory = builder.build();
        validator = factory.getValidator(Customer.class);
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

        AgimatecValidatorFactoryBuilder builder = Validation
                .builderType(AgimatecValidatorFactoryBuilder.class)
                .providerResolver(resolver)
                .getBuilder();
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

        ValidatorFactoryBuilder<?> builder = Validation
                .defineBootstrapState()
                .providerResolver(resolver)
                .getBuilder();
        assertDefaultBuilderAndFactory(builder);
    }

    private void assertDefaultBuilderAndFactory(ValidatorFactoryBuilder builder) {
        assertNotNull(builder);
        assertTrue(builder instanceof FactoryBuilderImpl);

        ValidatorFactory factory = builder.build();
        assertNotNull(factory);
        assertTrue(factory instanceof AgimatecValidatorFactory);
    }

    public void testFailingCustomResolver() {
        ValidationProviderResolver resolver = new ValidationProviderResolver() {

            public List<ValidationProvider> getValidationProviders() {
                return new ArrayList<ValidationProvider>();
            }
        };

        SpecializedBuilderFactory<AgimatecValidatorFactoryBuilder> type =
                Validation.builderType(AgimatecValidatorFactoryBuilder.class);

        final SpecializedBuilderFactory<AgimatecValidatorFactoryBuilder> specializedBuilderFactory =
                type.providerResolver(resolver);

        try {
            specializedBuilderFactory.getBuilder();
            fail();
        }
        catch (ValidationException e) {
            assertEquals(
                    "Wrong error message",
                    "Unable to find provider: interface " +
                            AgimatecValidatorFactoryBuilder.class.getName(),
                    e.getMessage()
            );
        }
    }

    class BadlyBehavedNotNullConstraint extends NotNullConstraint {
        @Override
        public boolean isValid(Object object, ConstraintContext context) {
            return true;
        }
    }
}
