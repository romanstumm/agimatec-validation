package com.agimatec.validation.jsr303;

import com.agimatec.validation.constraints.LengthConstraint;
import com.agimatec.validation.example.BusinessObject;
import com.agimatec.validation.jsr303.example.Address;
import com.agimatec.validation.jsr303.example.Book;
import com.agimatec.validation.jsr303.example.Engine;
import junit.framework.TestCase;

import javax.validation.*;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 15:12:29 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class Jsr303Test extends TestCase {
    static {
        AgimatecValidatorFactory.getDefault().getMetaBeanManager()
                .addResourceLoader("com/agimatec/validation/example/test-beanInfos.xml");
    }

    public void testBook() {
        Validator validator = getValidator();

        BusinessObject object = new BusinessObject();
        object.setTitle("1234567834567 too long title ");
        Set<ConstraintViolation<BusinessObject>> violations = validator.validate(object);
        assertNotNull(violations);
        assertTrue(!violations.isEmpty());

        assertTrue(!validator.validateProperty(object, "title").isEmpty());
    }

    public void testValidateValue() {
        assertTrue(getValidator()
                .validateValue(Book.class, "subtitle", "123456789098765432").isEmpty());
        assertFalse(getValidator()
                .validateValue(Book.class, "subtitle",
                        "123456789098765432123456789098765432123456789098765432").isEmpty());
    }

    public void testMetadataAPI_Book() {
        Validator validator = getValidator();
        assertNotNull(validator.getConstraintsForClass(Book.class));
        assertTrue(validator.getConstraintsForClass(Book.class) == validator.getConstraintsForClass(Book.class));
        BeanDescriptor bc = validator.getConstraintsForClass(Book.class);
//        assertEquals(ElementType.TYPE, bc.getElementType());
        assertEquals(Book.class, bc.getType());
//        assertEquals(false, bc.isCascaded());
//        assertEquals("", bc.getPropertyPath());
        assertTrue(bc.getConstraintDescriptors() != null);
    }

    public void testMetadataAPI_Engine() {
        Validator validator = getValidator();
        assertTrue(validator.getPropertiesWithConstraints(Engine.class).contains("serialNumber"));
        ElementDescriptor desc = validator.getConstraintsForProperty(Engine.class, "serialNumber");
//        assertEquals(ElementType.FIELD, desc.getElementType());
        assertEquals(String.class, desc.getType());
    }

    public void testMetadataAPI_Address() {
        Validator validator = getValidator();
        assertFalse(validator.getConstraintsForClass(Address.class).getConstraintDescriptors().isEmpty());

        Set<String> props = validator.getPropertiesWithConstraints(Address.class);
        assertTrue(props.contains("addressline1")); // annotated at field level
        assertTrue(props.contains("addressline2"));
        assertTrue(props.contains("zipCode"));
        assertTrue(props.contains("country"));
        assertTrue(props.contains("city"));       // annotated at method level
        assertEquals(5, props.size());

        ElementDescriptor desc = validator.getConstraintsForProperty(Address.class, "addressline1");
        assertNotNull(desc);
        boolean found = false;
        for (ConstraintDescriptor each : desc.getConstraintDescriptors()) {
            if (each.getConstraintClass().equals(LengthConstraint.class)) {
                assertTrue(each.getParameters().containsKey("max"));
                assertEquals(30, each.getParameters().get("max"));
                found = true;
            }
        }
        assertTrue(found);

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
