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

    public void testCreateResolver() {

        final Validator gvalidator = getValidator();
        MessageInterpolator.Context ctx = new MessageInterpolator.Context() {

            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return (ConstraintDescriptor<?>) gvalidator
                      .getConstraintsForClass(PreferredGuest.class).
                      getConstraintsForProperty("guestCreditCardNumber")
                      .getConstraintDescriptors().toArray()[0];
            }

            public Object getValidatedValue() {
                return "12345678";
            }
        };
        String msg = interpolator.interpolate("{validator.creditcard}", ctx);
        Assert.assertEquals("credit card is not valid", msg);

        ctx = new MessageInterpolator.Context() {
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return (ConstraintDescriptor) gvalidator
                      .getConstraintsForClass(Author.class).
                      getConstraintsForProperty("lastName")
                      .getConstraintDescriptors().toArray()[0];
            }

            public Object getValidatedValue() {
                return "";
            }
        };

        msg = interpolator.interpolate("{constraint.notEmpty}", ctx);
        Assert.assertEquals("may not be empty", msg);
    }


    private Validator getValidator() {
        return AgimatecValidatorFactory.getDefault().getValidator();
    }
}
