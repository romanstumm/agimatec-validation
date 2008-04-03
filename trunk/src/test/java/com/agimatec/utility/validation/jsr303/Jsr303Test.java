package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.example.BusinessObject;
import junit.framework.TestCase;

import javax.validation.*;
import java.util.Set;
import java.lang.annotation.ElementType;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 15:12:29 <br/>
 *
 */
public class Jsr303Test extends TestCase {
    static {
        Provider.getInstance().getMetaBeanManager()
                .addResourceLoader("com/agimatec/utility/validation/example/test-beanInfos.xml");
    }

    public void testBook() {
        Validator<BusinessObject> validator = getValidator(BusinessObject.class);

        BusinessObject object = new BusinessObject();
        object.setTitle("1234567834567 too long title ");
        Set<InvalidConstraint<BusinessObject>> violations = validator.validate(object);
        assertNotNull(violations);
        assertTrue(!violations.isEmpty());
    }

    public void testMetadataAPI_Book() {
        Validator<Book> validator = getValidator(Book.class);
        assertNotNull(validator.getBeanConstraints());
        assertTrue(validator.getBeanConstraints() == validator.getBeanConstraints());
        ElementDescriptor bc = validator.getBeanConstraints();
        assertEquals(ElementType.TYPE, bc.getElementType());
        assertEquals(Book.class, bc.getReturnType());
        assertEquals(false, bc.isCascaded());
        assertEquals("", bc.getPropertyPath());
        assertTrue(bc.getConstraintDescriptors() != null);
    }

    public void testMetadataAPI_Engine() {
        Validator validator = getValidator(Engine.class);
        assertTrue(validator.getValidatedProperties().contains("serialNumber"));
        ElementDescriptor desc = validator.getConstraintsForProperty("serialNumber");
        assertEquals(ElementType.FIELD, desc.getElementType());
        assertEquals(String.class, desc.getReturnType());
    }

    public void testMetadataAPI_Address() {
        Validator validator = getValidator(Address.class);
        assertFalse(validator.getBeanConstraints().getConstraintDescriptors().isEmpty());

        Set<String> props = validator.getValidatedProperties();
        assertTrue(props.contains("addressline1")); // annotated at field level
        assertTrue(props.contains("addressline2"));
        assertTrue(props.contains("zipCode"));
        assertTrue(props.contains("country"));
        assertTrue(props.contains("city"));       // annotated at method level
        assertEquals(5, props.size());

        ElementDescriptor desc = validator.getConstraintsForProperty("addressline1");
        assertNotNull(desc);
        boolean found = false;
        for(ConstraintDescriptor each : desc.getConstraintDescriptors()) {
            if(each.getConstraintImplementation() instanceof LengthConstraint) {
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
