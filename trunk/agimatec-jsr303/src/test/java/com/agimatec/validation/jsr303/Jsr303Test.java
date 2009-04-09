package com.agimatec.validation.jsr303;

import com.agimatec.validation.constraints.SizeValidator;
import com.agimatec.validation.jsr303.example.Address;
import com.agimatec.validation.jsr303.example.Book;
import com.agimatec.validation.jsr303.example.Engine;
import com.agimatec.validation.jsr303.example.Second;
import junit.framework.Assert;
import junit.framework.TestCase;

import javax.validation.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 15:12:29 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class Jsr303Test extends TestCase {
/*    static {
        AgimatecValidatorFactory.getDefault().getMetaBeanManager()
                .addResourceLoader("com/agimatec/validation/example/test-beanInfos.xml");
    }*/

    /*  public void testUseAgimatecXmlMetaData() {
        Validator validator = getValidator();

        BusinessObject object = new BusinessObject();
        object.setTitle("1234567834567 too long title ");
        Set<ConstraintViolation<BusinessObject>> violations = validator.validate(object);
        Assert.assertNotNull(violations);
        Assert.assertTrue(!violations.isEmpty());

        Assert.assertTrue(!validator.validateProperty(object, "title").isEmpty());
    }*/

    public void testValidateValue() {
        Assert.assertTrue(getValidator()
              .validateValue(Book.class, "subtitle", "123456789098765432").isEmpty());
        Assert.assertFalse(getValidator()
              .validateValue(Book.class, "subtitle",
                    "123456789098765432123412345678909876543212341234564567890987654321234",
                    Second.class).isEmpty());
    }

    public void testMetadataAPI_Book() {
        Validator validator = getValidator();
        Assert.assertNotNull(validator.getConstraintsForClass(Book.class));
        Assert.assertTrue(validator.getConstraintsForClass(Book.class) ==
              validator.getConstraintsForClass(Book.class));
        BeanDescriptor bc = validator.getConstraintsForClass(Book.class);
//        assertEquals(ElementType.TYPE, bc.getElementType());
        Assert.assertEquals(Book.class, bc.getType());
//        assertEquals(false, bc.isCascaded());
//        assertEquals("", bc.getPropertyPath());
        Assert.assertTrue(bc.getConstraintDescriptors() != null);
    }

    public void testMetadataAPI_Engine() {
        Validator validator = getValidator();
        ElementDescriptor desc = validator.getConstraintsForClass(Engine.class)
              .getConstraintsForProperty("serialNumber");
        assertNotNull(desc);
//        assertEquals(ElementType.FIELD, desc.getElementType());
        Assert.assertEquals(String.class, desc.getType());
    }

    public void testMetadataAPI_Address() {
        Validator validator = getValidator();
        Assert.assertFalse(validator.getConstraintsForClass(Address.class)
              .getConstraintDescriptors().isEmpty());

        Set<PropertyDescriptor> props =
              validator.getConstraintsForClass(Address.class).getConstrainedProperties();
        Set<String> propNames = new HashSet(props.size());
        for (PropertyDescriptor each : props) {
            propNames.add(each.getPropertyName());
        }
        Assert.assertTrue(propNames.contains("addressline1")); // annotated at field level
        Assert.assertTrue(propNames.contains("addressline2"));
        Assert.assertTrue(propNames.contains("zipCode"));
        Assert.assertTrue(propNames.contains("country"));
        Assert.assertTrue(propNames.contains("city"));       // annotated at method level
        Assert.assertEquals(5, props.size());

        ElementDescriptor desc = validator.getConstraintsForClass(Address.class)
              .getConstraintsForProperty("addressline1");
        Assert.assertNotNull(desc);
        boolean found = false;
        for (ConstraintDescriptor each : desc.getConstraintDescriptors()) {
            if (each.getConstraintValidatorClasses().get(0).equals(SizeValidator.class)) {
                Assert.assertTrue(each.getAttributes().containsKey("max"));
                assertEquals(30, each.getAttributes().get("max"));
                found = true;
            }
        }
        Assert.assertTrue(found);

    }

    public void testEngine() {
        Validator validator = getValidator();
        Engine engine = new Engine();
        validator.validate(engine);
    }

    private Validator getValidator() {
        return AgimatecValidatorFactory.getDefault().getValidator();
    }
}
