package com.agimatec.validation.jsr303;

import com.agimatec.validation.example.BusinessObject;
import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;

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
        Provider.getInstance().getMetaBeanManager()
                .addResourceLoader("com/agimatec/validation/example/test-beanInfos.xml");
    }

    public void testBook() {
        Validator<BusinessObject> validator = getValidator(BusinessObject.class);

        BusinessObject object = new BusinessObject();
        object.setTitle("1234567834567 too long title ");
        Set<InvalidConstraint<BusinessObject>> violations = validator.validate(object);
        assertNotNull(violations);
        assertTrue(!violations.isEmpty());

        assertTrue(!validator.validateProperty(object, "title").isEmpty());
    }

    public void testValidateValue() {
        assertTrue(getValidator(Book.class)
                .validateValue("subtitle", "123456789098765432").isEmpty());
        assertFalse(getValidator(Book.class)
                .validateValue("subtitle", "123456789098765432123456789098765432123456789098765432").isEmpty());
    }

    public void testMetadataAPI_Book() {
        Validator<Book> validator = getValidator(Book.class);
        assertNotNull(validator.getConstraintsForBean());
        assertTrue(validator.getConstraintsForBean() == validator.getConstraintsForBean());
        ElementDescriptor bc = validator.getConstraintsForBean();
//        assertEquals(ElementType.TYPE, bc.getElementType());
        assertEquals(Book.class, bc.getType());
        assertEquals(false, bc.isCascaded());
        assertEquals("", bc.getPropertyPath());
        assertTrue(bc.getConstraintDescriptors() != null);
    }

    public void testMetadataAPI_Engine() {
        Validator validator = getValidator(Engine.class);
        assertTrue(ArrayUtils.contains(validator.getValidatedProperties(), "serialNumber"));
        ElementDescriptor desc = validator.getConstraintsForProperty("serialNumber");
//        assertEquals(ElementType.FIELD, desc.getElementType());
        assertEquals(String.class, desc.getType());
    }

    public void testMetadataAPI_Address() {
        Validator validator = getValidator(Address.class);
        assertFalse(validator.getConstraintsForBean().getConstraintDescriptors().isEmpty());

        String[] props = validator.getValidatedProperties();
        assertTrue(ArrayUtils.contains(props,"addressline1")); // annotated at field level
        assertTrue(ArrayUtils.contains(props,"addressline2"));
        assertTrue(ArrayUtils.contains(props,"zipCode"));
        assertTrue(ArrayUtils.contains(props,"country"));
        assertTrue(ArrayUtils.contains(props,"city"));       // annotated at method level
        assertEquals(5, props.length);

        ElementDescriptor desc = validator.getConstraintsForProperty("addressline1");
        assertNotNull(desc);
        boolean found = false;
        for (ConstraintDescriptor each : desc.getConstraintDescriptors()) {
            if (each.getContstraintClass().equals(LengthConstraint.class)) {
                assertTrue(each.getParameters().containsKey("max"));
                assertEquals(30, each.getParameters().get("max"));
                found = true;
            }
        }
        assertTrue(found);

    }

    public void testEngine() {
        Validator<Engine> validator = getValidator(Engine.class);
        Engine engine = new Engine();
        validator.validate(engine);
    }

    private Validator getValidator(Class clazz) {
        return ValidationProviderFactory.createValidator(clazz);
    }
}
