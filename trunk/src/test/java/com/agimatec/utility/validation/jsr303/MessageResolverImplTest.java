package com.agimatec.utility.validation.jsr303;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import javax.validation.*;

/**
 * MessageResolverImpl Tester.
 *
 * @author ${USER}
 * @version 1.0
 * @since <pre>04/02/2008</pre>
 */
public class MessageResolverImplTest extends TestCase {
    MessageResolverImpl resolver = new MessageResolverImpl();

    public MessageResolverImplTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(MessageResolverImplTest.class);
    }

    public void testCreateResolver() {
        assertTrue(resolver.getDefaultBundle() != null);

        Validator<PreferredGuest> gvalidator = getValidator(PreferredGuest.class);
        ConstraintDescriptor desc =
                (ConstraintDescriptor) gvalidator.getConstraintsForProperty("guestCreditCardNumber")
                        .getConstraintDescriptors().toArray()[0];
        String msg = resolver.interpolate("{beancheck.creditcard}", desc, "12345678");
        assertEquals("credit card is not valid", msg);

        Validator<Author> avalidator = getValidator(Author.class);
        desc = (ConstraintDescriptor) avalidator.getConstraintsForProperty("lastName")
                .getConstraintDescriptors().toArray()[0];

        msg = resolver.interpolate("{beancheck.notEmpty}", desc, "");
        assertEquals("may not be null or empty", msg);
    }


    private Validator getValidator(Class clazz) {
        return ValidationProviderFactory.createValidator(clazz);
    }
}
