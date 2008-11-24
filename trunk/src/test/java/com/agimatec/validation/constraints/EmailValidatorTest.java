package com.agimatec.validation.constraints;

import com.agimatec.validation.jsr303.AgimatecValidatorFactory;
import com.agimatec.validation.jsr303.example.Customer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.validation.Validator;

/**
 * EmailValidator Tester.
 *
 * @author Roman Stumm
 * @version 1.0
 * @since <pre>10/14/2008</pre>
 */
public class EmailValidatorTest extends TestCase {
    public EmailValidatorTest(String name) {
        super(name);
    }

    public void testEmail() {
        Validator validator = AgimatecValidatorFactory.getDefault().getValidator();
        Customer customer = new Customer();
        customer.setCustomerId("id-1");
        customer.setFirstName("Mary");
        customer.setLastName("Do");

        assertEquals(0, validator.validate(customer).size());

        customer.setEmailAddress("some@invalid@address");
        assertEquals(1, validator.validate(customer).size());

        customer.setEmailAddress("some.valid-012345@address_at-test.org");
        assertEquals(0, validator.validate(customer).size());
    }


    public static Test suite() {
        return new TestSuite(EmailValidatorTest.class);
    }
}
