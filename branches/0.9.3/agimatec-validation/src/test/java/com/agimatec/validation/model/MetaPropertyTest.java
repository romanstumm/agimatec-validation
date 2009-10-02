package com.agimatec.validation.model;

import com.agimatec.validation.example.BusinessEnum;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * MetaProperty Tester.
 *
 * @author <Authors name>
 * @since <pre>02/12/2009</pre>
 * @version 1.0
 */
public class MetaPropertyTest extends TestCase {
    public MetaPropertyTest(String name) {
        super(name);
    }


    public void testGetTypeClass() throws Exception {
        MetaProperty prop = new MetaProperty();
        prop.setType(String.class);
        assertEquals(String.class, prop.getTypeClass());
        assertEquals(String.class, prop.getType());
        prop.setType(new DynaTypeEnum(BusinessEnum.class, BusinessEnum.VALUE1.name(),
              BusinessEnum.VALUE3.name()));
        assertEquals(BusinessEnum.class, prop.getTypeClass());
        assertEquals(2, ((DynaTypeEnum)prop.getType()).getEnumConstants().length);
    }


    public static Test suite() {
        return new TestSuite(MetaPropertyTest.class);
    }
}
