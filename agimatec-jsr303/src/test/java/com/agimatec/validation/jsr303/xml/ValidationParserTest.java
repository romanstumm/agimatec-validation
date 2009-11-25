package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.jsr303.AgimatecValidationProvider;
import com.agimatec.validation.jsr303.AgimatecValidatorConfiguration;
import com.agimatec.validation.jsr303.ConfigurationImpl;
import com.agimatec.validation.jsr303.SimpleTraversableResolver;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * ValidationParser Tester.
 *
 * @author <Authors name>
 * @since <pre>11/25/2009</pre>
 * @version 1.0
 */
public class ValidationParserTest extends TestCase {
    public ValidationParserTest(String name) {
        super(name);
    }

    public void testParse() {
        ValidationParser vp = new ValidationParser("sample-validation.xml");
        ConfigurationImpl config = new ConfigurationImpl(null,
              new AgimatecValidationProvider());
        vp.processValidationConfig(config);
    }

    public void testConfigureFromXml() {
        AgimatecValidatorConfiguration config =
              Validation.byProvider(AgimatecValidationProvider.class).configure();
        config.addProperty(AgimatecValidatorConfiguration.PROPERTY_VALIDATION_XML_PATH, 
              "sample-validation.xml");
        ValidatorFactory factory = config.buildValidatorFactory();
        assertTrue(factory.getMessageInterpolator() instanceof TestMessageInterpolator);
        assertTrue(factory.getConstraintValidatorFactory() instanceof TestConstraintValidatorFactory);
        assertTrue(factory.getTraversableResolver() instanceof SimpleTraversableResolver);
        Validator validator = factory.getValidator();
        assertNotNull(validator);
    }

    public static Test suite() {
        return new TestSuite(ValidationParserTest.class);
    }
}
