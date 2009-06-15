package com.agimatec.validation.constraints;

import junit.framework.Assert;
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
public class DigitsValidatorTest extends TestCase {
    public DigitsValidatorTest(String name) {
        super(name);
    }

    public void testValidateNumber() {
        DigitsValidator validator = new DigitsValidator();
        validator.setFractional(4);
        validator.setIntegral(2);
        BigDecimal val = new BigDecimal("100.12345");
        Assert.assertFalse(validator.isValid(val, null));
        val = new BigDecimal("99.1234");
        Assert.assertTrue(validator.isValid(val, null));
    }

    public void testValidateString() {
        DigitsValidator validator = new DigitsValidator();
        validator.setFractional(4);
        validator.setIntegral(2);
        String val = "100.12345";
        Assert.assertFalse(validator.isValid(val, null));
        val = "99.1234";
        Assert.assertTrue(validator.isValid(val, null));
    }

    public void testValidateNumber2() {
        DigitsValidator validator = new DigitsValidator();
        validator.setFractional(4);
        validator.setIntegral(2);
        Long val = new Long("100");
        Assert.assertFalse(validator.isValid(val, null));
        val = new Long("99");
        Assert.assertTrue(validator.isValid(val, null));
    }

    public void testValidateString2() {
        DigitsValidator validator = new DigitsValidator();
        validator.setFractional(0);
        validator.setIntegral(2);
        String val = "99.5";
        Assert.assertFalse(validator.isValid(val, null));
        val = "99";
        Assert.assertTrue(validator.isValid(val, null));
    }

    public static Test suite() {
        return new TestSuite(DigitsValidatorTest.class);
    }
}
