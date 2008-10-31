package com.agimatec.utility.validation.integration;

import com.agimatec.utility.validation.MetaBeanManagerFactory;
import com.agimatec.utility.validation.ValidationResults;
import com.agimatec.utility.validation.example.BusinessObject;
import com.agimatec.utility.validation.example.ExampleBusinessObjectService;
import com.agimatec.utility.validation.xml.XMLMetaBeanURLLoader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.reflect.Method;

/**
 * ThreadBeanValidator Tester.
 *
 * @author ${USER}
 * @version 1.0
 * @since <pre>07/09/2007</pre>
 *        Copyright: Agimatec GmbH 2008
 */
public class ThreadBeanValidatorTest extends TestCase {
    private ThreadBeanValidator validator;

    public ThreadBeanValidatorTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        validator = new ThreadBeanValidator();
        MetaBeanManagerFactory.getRegistry().addLoader(
                new XMLMetaBeanURLLoader(BusinessObject.class.getResource("test-beanInfos.xml")));
    }

    public static Test suite() {
        return new TestSuite(ThreadBeanValidatorTest.class);
    }

    public void testValidate() {
        BusinessObject object = createExampleObject();
        ValidationResults results = validator.validate(object);
        assertTrue(!results.isEmpty());
        results = (ValidationResults) ThreadValidationContext.getCurrent().getListener();
        assertTrue(!results.isEmpty());
    }

    public void testValidateAnnotation() throws NoSuchMethodException {
        Class serviceClass = ExampleBusinessObjectService.class;
        Method method = serviceClass
                .getMethod("saveBusinessObject", BusinessObject.class, Object.class);
        assertNotNull(method);
        Object[] params = {createExampleObject(), null};
        assertTrue(!validator.validateCall(method, params).isEmpty());
    }

    public void testValidateArrayParameter() throws NoSuchMethodException {
        Class serviceClass = ExampleBusinessObjectService.class;
        Method method = serviceClass
                .getMethod("saveBusinessObjects", BusinessObject[].class);
        assertNotNull(method);
        Object[] params = {new BusinessObject[]{createExampleObject(), createExampleObject()}};
        assertTrue(!validator.validateCall(method, params).isEmpty());
    }

    private BusinessObject createExampleObject() {
        BusinessObject object = new BusinessObject();
        object.setUserId(2L);
        object.setFirstName("FirstName");
//        object.setLastName("LastName");
        return object;
    }
}
