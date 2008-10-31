package com.agimatec.utility.validation;

import com.agimatec.utility.validation.model.MetaProperty;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ValidationResults Tester.
 *
 * @author ${USER}
 * @since <pre>07/06/2007</pre>
 * @version 1.0
 * Copyright: Agimatec GmbH 2008
 */
public class ValidationResultsTest extends TestCase {
    private ValidationResults results;

    public ValidationResultsTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        results = new ValidationResults();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testValidationResults() throws Exception {
        assertTrue(results.isEmpty());
        BeanValidationContext ctx = new BeanValidationContext(results);
        ctx.setBean(this);
        ctx.setMetaProperty(new MetaProperty());
        ctx.getMetaProperty().setName("prop");
        results.addError("test", ctx);
        assertFalse(results.isEmpty());
        assertTrue(results.hasErrorForReason("test"));
        assertTrue(results.hasError(this, "prop"));
        assertTrue(results.hasError(this, null));
        assertFalse(results.hasError(this, "prop2"));
    }

    public static Test suite() {
        return new TestSuite(ValidationResultsTest.class);
    }
}
