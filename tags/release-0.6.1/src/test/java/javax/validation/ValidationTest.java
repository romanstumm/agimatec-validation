package javax.validation;

import com.agimatec.utility.validation.jsr303.ClassValidator;
import com.agimatec.utility.validation.jsr303.GroupBeanValidationContext;
import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 11:48:37 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ValidationTest extends TestCase {
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();   // call super!
        GroupBeanValidationContext.INCLUDE_INDEX_IN_PROPERTY_PATH = false;
    }

    public void testBook() {
        Validator validator = getValidator(Book.class);
        Author author = new Author();
        author.setLastName("Baudelaire");
        author.setFirstName("");
        Book book = new Book();
        book.setAuthor(author);
        book.setSubtitle("12345678900125678901234578901234567890");

        // NotEmpty failure on the title field
        Set<InvalidConstraint> errors = validator.validate(book);
        assertTrue(!errors.isEmpty());

        book.setTitle("Les fleurs du mal");
        author.setCompany("Some random publisher with a very very very long name");

        // author.firstName fails to pass the NotEmpty constraint
        //  author.company fails to pass the Length constraint
    }

    /**
     * test:
     * - dynamic resolution of associated object types.
     * - inheritance of validation constraints
     * - complex valiation, different groups, nested object net
     */
    public void testValidAnnotation() {
        Author a = new Author();
        a.setAddresses(new ArrayList());
        BusinessAddress adr = new BusinessAddress();
        adr.setCountry(new Country());
        adr.setAddressline1("line1");
        adr.setAddressline2("line2");

        adr.setZipCode("1234567890123456789");
        a.getAddresses().add(adr);

        a.setFirstName("Karl");
        a.setLastName("May");

        Validator v = getValidator(a.getClass());
        Set found = v.validate(a, "default", "first", "last");
        assertTrue(!found.isEmpty());
        assertEquals(4, found.size());

        adr.setCity("Berlin");
        adr.setZipCode("12345");
        adr.setCompany("agimatec GmbH");
        found = v.validate(a, "default", "first", "last");
        assertEquals(1, found.size());
        InvalidConstraint ic = (InvalidConstraint) found.iterator().next();
        if (GroupBeanValidationContext.INCLUDE_INDEX_IN_PROPERTY_PATH) {
            assertEquals("addresses[0].country.name", ic.getPropertyPath());
        } else {
            assertEquals("addresses.country.name", ic.getPropertyPath());
        }
    }

    public void testPropertyPathWithIndex() {
        GroupBeanValidationContext.INCLUDE_INDEX_IN_PROPERTY_PATH = true;
        Author a = new Author();
        a.setAddresses(new ArrayList());
        Address adr = new Address();
        adr.setAddressline1("adr1");
        adr.setCity("Santiago");
        a.getAddresses().add(adr);
        adr = new Address();
        adr.setAddressline1("adr2");
        adr.setCity("Havanna");
        a.getAddresses().add(adr);
        adr = new Address();
        adr.setAddressline1("adr3");
        adr.setCity("Trinidad");
        a.getAddresses().add(adr);

        Set<InvalidConstraint> constraints = getValidator(Author.class).validate(a);
        assertTrue(!constraints.isEmpty());

        assertPropertyPath("addresses[0].country", constraints);
        assertPropertyPath("addresses[1].country", constraints);
        assertPropertyPath("addresses[2].country", constraints);
    }

    private void assertPropertyPath(String propertyPath, Set<InvalidConstraint> constraints) {
        for (InvalidConstraint each : constraints) {
            if (each.getPropertyPath().equals(propertyPath)) return;
        }
        fail(propertyPath + " not found in " + constraints);
    }

    public void testPropertyPathRecursive() {
        GroupBeanValidationContext.INCLUDE_INDEX_IN_PROPERTY_PATH = true;
        RecursiveFoo foo1 = new RecursiveFoo();
        RecursiveFoo foo11 = new RecursiveFoo();
        foo1.getFoos().add(foo11);
        RecursiveFoo foo12 = new RecursiveFoo();
        foo1.getFoos().add(foo12);
        RecursiveFoo foo2 = new RecursiveFoo();
        foo11.getFoos().add(foo2);

        Set<InvalidConstraint> constraints = getValidator(RecursiveFoo.class).validate(foo1);
        // TODO RSt - clarify behavior!
        // not deterministic: both could be true sometimes...
//        assertPropertyPath("foos[0].foos[0].foos", constraints);
//        assertPropertyPath("foos[1].foos[0].foos", constraints);

        // not deterministic: both could be true sometimes...
//        assertPropertyPath("foos[1].foos", constraints);
//        assertPropertyPath("foos[0].foos", constraints);
    }

    public void testNullElementInCollection() {
        try {
            getValidator(RecursiveFoo.class).validate(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        RecursiveFoo foo = new RecursiveFoo();
        foo.getFoos().add(new RecursiveFoo());
        foo.getFoos().add(null);
        assertTrue(!getValidator(RecursiveFoo.class).validate(foo).isEmpty());
        // check that no nullpointer exception gets thrown
    }

    private Validator getValidator(Class clazz) {
        return new ClassValidator(clazz);
    }

    public void testGroups() {
        Validator validator = getValidator(Book.class);
        Author author = new Author();
        author.setCompany("ACME");
        Book book = new Book();
        book.setTitle("");
        book.setAuthor(author);
        Set<InvalidConstraint> invalidConstraints = validator.validate(book);
        //assuming an english locale, the interpolated message is returned
        for (InvalidConstraint invalidConstraint : invalidConstraints) {
            if (invalidConstraint.getBeanClass() == Book.class) {
                assertTrue("may not be null or empty".equals(invalidConstraint.getMessage()));
                assertTrue(book == invalidConstraint.getRootBean());
                assertTrue(Book.class.equals(invalidConstraint.getBeanClass()));
                //the offending value
                assertTrue(book.getTitle().equals(invalidConstraint.getValue()));
                //the offending property
                assertTrue("title".equals(invalidConstraint.getPropertyPath()));
                assertTrue(invalidConstraint.getGroups().length == 1);
                List expectedGroups = new ArrayList(1);
                expectedGroups.add("first");
                for (String group : invalidConstraint.getGroups()) {
                    assertTrue(expectedGroups.contains(group));
                }
            }

            if (invalidConstraint.getBeanClass() == Author.class) {
                // The second failure, NotEmpty on the author's lastname, will produce the following InvalidConstraint object:
                assertTrue("may not be null or empty".equals(invalidConstraint.getMessage()));
                assertTrue(book == invalidConstraint.getRootBean());
                assertTrue(Author.class == invalidConstraint.getBeanClass());
                //the offending value
                assertTrue(book.getAuthor().getLastName() == invalidConstraint.getValue());
                //the offending property
                assertTrue("author.lastName".equals(invalidConstraint.getPropertyPath()));
            }
        }
    }

    public void testMetadataAPI() {
        Validator bookValidator = getValidator(Book.class);

        assertTrue(bookValidator.hasConstraints());
        ElementDescriptor bookBeanDescriptor = bookValidator.getBeanConstraints();
        assertTrue(bookBeanDescriptor.getElementType() == ElementType.TYPE);
        assertTrue(bookBeanDescriptor.getConstraintDescriptors().size() == 0); //no constraint
        assertTrue("".equals(bookBeanDescriptor.getPropertyPath())); //root element
        //more specifically "author" and "title"
        assertTrue(bookValidator.getValidatedProperties().size() == 3);
        //not a property
        assertTrue(bookValidator.getConstraintsForProperty("doesNotExist") == null);
        //property with no constraint
        assertTrue(bookValidator.getConstraintsForProperty("description") == null);
        ElementDescriptor propertyDescriptor = bookValidator.getConstraintsForProperty("title");
        assertTrue(propertyDescriptor.getElementType() == ElementType.METHOD);
        assertTrue(propertyDescriptor.getConstraintDescriptors().size() == 1);
        assertTrue("title".equals(propertyDescriptor.getPropertyPath()));
        //assuming the implementation returns the NotEmpty constraint first
        ConstraintDescriptor constraintDescriptor = propertyDescriptor.getConstraintDescriptors()
                .iterator().next();
        assertTrue(constraintDescriptor.getAnnotation().annotationType().equals(NotEmpty.class));
        assertTrue(constraintDescriptor.getGroups().size() == 1); //"first"
        assertTrue(
                constraintDescriptor.getConstraintImplementation() instanceof StandardConstraint);
        StandardConstraint standardConstraint =
                (StandardConstraint) constraintDescriptor.getConstraintImplementation();
        //@NotEmpty cannot be null
        assertTrue(!standardConstraint.getStandardConstraintDescriptor().getNullability());
        //assuming the implementation returns the Length constraint first
        bookBeanDescriptor = bookValidator.getConstraintsForProperty("subtitle");
        Iterator<ConstraintDescriptor> iterator =
                bookBeanDescriptor.getConstraintDescriptors().iterator();
        constraintDescriptor = iterator.next();
        assertTrue(constraintDescriptor.getAnnotation().annotationType().equals(Length.class));
        assertTrue(((Integer) constraintDescriptor.getParameters().get("max")) == 30);
        assertTrue(constraintDescriptor.getGroups().size() == 1);
        propertyDescriptor = bookValidator.getConstraintsForProperty("author");
        assertEquals(ElementType.FIELD, propertyDescriptor.getElementType());
        assertTrue(propertyDescriptor.getConstraintDescriptors().size() == 1);
        assertTrue(propertyDescriptor.isCascaded());
    }
}
