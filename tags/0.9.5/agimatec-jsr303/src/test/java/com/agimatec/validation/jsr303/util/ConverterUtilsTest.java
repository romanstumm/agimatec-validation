package com.agimatec.validation.jsr303.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.validation.ValidationException;

/**
 * ConverterUtils Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>11/25/2009</pre>
 */
public class ConverterUtilsTest extends TestCase {
    public ConverterUtilsTest(String name) {
        super(name);
    }

    public void testLong() {
        long lng = (Long) ConverterUtils.fromStringToType("444", long.class);
        assertEquals(444L, lng);

        try {
            ConverterUtils.fromStringToType("hallo", long.class);
            fail();
        } catch (ValidationException ve) {
            // yes
        }
    }

    public void testClass() {
        assertEquals(getClass(),
              ConverterUtils.fromStringToType(getClass().getName(), Class.class));
    }

    public void testEnum() {
        Thread.State state = (Thread.State) ConverterUtils
              .fromStringToType(Thread.State.TERMINATED.name(), Thread.State.class);
        assertEquals(Thread.State.TERMINATED, state);
    }

    public static Test suite() {
        return new TestSuite(ConverterUtilsTest.class);
    }
}
