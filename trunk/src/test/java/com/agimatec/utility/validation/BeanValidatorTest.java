package com.agimatec.utility.validation;

import com.agimatec.utility.validation.example.BusinessObject;
import com.agimatec.utility.validation.example.BusinessObjectAddress;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.routines.Reasons;
import com.agimatec.utility.validation.xml.XMLMetaBeanURLLoader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * BeanValidator Tester.
 *
 * @author ${USER}
 * @version 1.0
 * @since <pre>07/06/2007</pre>
 * Copyright: Agimatec GmbH 2008
 */
public class BeanValidatorTest extends TestCase {
    public BeanValidatorTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testValidate() {
        MetaBeanFinder finder = MetaBeanManagerFactory.getFinder();
        MetaBeanManagerFactory.getRegistry().addLoader(new XMLMetaBeanURLLoader(
                BusinessObject.class.getResource("test-beanInfos.xml")));
        MetaBean info = finder.findForClass(BusinessObject.class);
        BusinessObject object = new BusinessObject();
        object.setAddress(new BusinessObjectAddress());
        object.getAddress().setOwner(object);
        BeanValidator validator = new BeanValidator();
        ValidationResults results = validator.validate(object, info);
        assertTrue(results.hasErrorForReason(Reasons.MANDATORY));
        assertTrue(results.hasError(object, null));
        assertTrue(results.hasError(object.getAddress(), null));

        object.setUserId(1L);
        object.setFirstName("Hans");
        object.setLastName("Tester");
        object.setAddress(new BusinessObjectAddress());
        object.getAddress().setOwner(object);
        assertFalse(validator.validate(object, info).isEmpty());

        object.getAddress().setCountry("0123456789012345678");
        assertFalse(validator.validate(object, info).isEmpty());

        object.getAddress().setCountry("Germany");
        object.setAddresses(new ArrayList());
        object.getAddresses().add(object.getAddress());
        object.getAddresses().add(object.getAddress());
        object.getAddresses().add(object.getAddress());
        assertTrue(validator.validate(object, info).isEmpty());

        // 4th address is too much!
        object.getAddresses().add(object.getAddress());
        assertFalse(validator.validate(object, info).isEmpty()); // cardinality error found
    }

    public static Test suite() {
        return new TestSuite(BeanValidatorTest.class);
    }
}
