package com.agimatec.validation.jsr303;

import com.agimatec.validation.jsr303.example.Author;
import com.agimatec.validation.jsr303.example.PreferredGuest;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.validation.ConstraintDescriptor;
import javax.validation.MessageInterpolator;
import javax.validation.Validator;
import java.util.Locale;

/**
 * MessageResolverImpl Tester.
 *
 * @author ${USER}
 * @version 1.0
 * @since <pre>04/02/2008</pre>
 *        Copyright: Agimatec GmbH 2008
 */
public class DefaultMessageInterpolatorTest extends TestCase {
    DefaultMessageInterpolator interpolator = new DefaultMessageInterpolator();

    public DefaultMessageInterpolatorTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(DefaultMessageInterpolatorTest.class);
    }

    public ConstraintDescriptor<?> getConstraintDescriptorCC() {
        return (ConstraintDescriptor<?>) getValidator()
              .getConstraintsForClass(PreferredGuest.class).
              getConstraintsForProperty("guestCreditCardNumber")
              .getConstraintDescriptors().toArray()[0];
    }

    public Object getValidatedValueCC() {
        return "12345678";
    }

    public ConstraintDescriptor<?> getConstraintDescriptorLN() {
        return (ConstraintDescriptor<?>) getValidator()
              .getConstraintsForClass(Author.class)
              .getConstraintsForProperty("lastName")
              .getConstraintDescriptors().toArray()[0];
    }

    public Object getValidatedValueLN() {
        return "";
    }

    public void testCreateResolver() {

        MessageInterpolator ctx = new MessageInterpolator() {

            public String interpolate(String message, ConstraintDescriptor constraint,
                Object value) {
                return interpolator.interpolate(message, constraint, value);
            }

            public String interpolate(String message, ConstraintDescriptor constraint,
                Object value, Locale locale) {
                return interpolator.interpolate(message, constraint, value,
                    locale);
            }
        };
        String msg = interpolator.interpolate("{test.validator.creditcard}",
            getConstraintDescriptorCC(),
            getValidatedValueCC());
        Assert.assertEquals("credit card is not valid", msg);

        msg = interpolator.interpolate("{com.agimatec.validation.constraints.NotEmpty.message}",
            getConstraintDescriptorLN(),
            getValidatedValueLN());
        Assert.assertEquals("may not be empty", msg);
    }


    private Validator getValidator() {
        return AgimatecValidatorFactory.getDefault().getValidator();
    }
}
