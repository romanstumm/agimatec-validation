package com.agimatec.validation.jsr303;

import com.agimatec.validation.jsr303.example.Author;
import com.agimatec.validation.jsr303.example.PreferredGuest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.validation.ConstraintDescriptor;
import javax.validation.Validator;

/**
 * MessageResolverImpl Tester.
 *
 * @author ${USER}
 * @version 1.0
 * @since <pre>04/02/2008</pre>
 *        Copyright: Agimatec GmbH 2008
 */
public class DefaultMessageResolverTest extends TestCase {
    DefaultMessageResolver resolver = new DefaultMessageResolver();

    public DefaultMessageResolverTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(DefaultMessageResolverTest.class);
    }

    public void testCreateResolver() {
        assertTrue(resolver.getDefaultBundle() != null);

        Validator gvalidator = getValidator();
        ConstraintDescriptor desc =
                (ConstraintDescriptor) gvalidator.getConstraintsForProperty(PreferredGuest.class,
                        "guestCreditCardNumber")
                        .getConstraintDescriptors().toArray()[0];
        String msg = resolver.interpolate("{validator.creditcard}", desc, "12345678");
        assertEquals("credit card is not valid", msg);

        Validator avalidator = getValidator();
        desc = (ConstraintDescriptor) avalidator.getConstraintsForProperty(Author.class, "lastName")
                .getConstraintDescriptors().toArray()[0];

        msg = resolver.interpolate("{validator.notEmpty}", desc, "");
        assertEquals("may not be null or empty", msg);
    }


    private Validator getValidator() {
        return AgimatecValidatorFactory.getDefault().getValidator();
    }
}
