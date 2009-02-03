package com.agimatec.validation.constraints;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.math.BigDecimal;

/**
 * DigitsConstraintValidator Tester.
 *
 * @author <Authors name>
 * @since <pre>02/03/2009</pre>
 * @version 1.0
 */
public class DigitsConstraintValidatorTest extends TestCase {
    public DigitsConstraintValidatorTest(String name) {
        super(name);
    }

    public void testValidateNumber() {
        DigitsConstraintValidator validator = new DigitsConstraintValidator();
        validator.setFractional(4);
        validator.setIntegral(2);
        BigDecimal val = new BigDecimal("100.12345");
        assertFalse(validator.isValid(val, null));
        val = new BigDecimal("99.1234");
        assertTrue(validator.isValid(val, null));
    }

    public void testValidateString() {
        DigitsConstraintValidator validator = new DigitsConstraintValidator();
        validator.setFractional(4);
        validator.setIntegral(2);
        String val = "100.12345";
        assertFalse(validator.isValid(val, null));
        val = "99.1234";
        assertTrue(validator.isValid(val, null));
    }

    public void testValidateNumber2() {
        DigitsConstraintValidator validator = new DigitsConstraintValidator();
        validator.setFractional(4);
        validator.setIntegral(2);
        Long val = new Long("100");
        assertFalse(validator.isValid(val, null));
        val = new Long("99");
        assertTrue(validator.isValid(val, null));
    }

    public void testValidateString2() {
        DigitsConstraintValidator validator = new DigitsConstraintValidator();
        validator.setFractional(0);
        validator.setIntegral(2);
        String val = "99.5";
        assertFalse(validator.isValid(val, null));
        val = "99";
        assertTrue(validator.isValid(val, null));
    }

    public static Test suite() {
        return new TestSuite(DigitsConstraintValidatorTest.class);
    }
}
