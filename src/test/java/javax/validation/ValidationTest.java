package javax.validation;

import com.agimatec.utility.validation.jsr303.ClassValidator;
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

    private Validator getValidator(Class clazz) {
        return new ClassValidator(clazz);
    }

    public void test2() {
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
                assertTrue( "may not be null or empty".equals(invalidConstraint.getMessage()));
                assertTrue( book == invalidConstraint.getRootBean());
                assertTrue( Author.class == invalidConstraint.getBeanClass());
                //the offending value
                assertTrue( book.getAuthor().getLastName() == invalidConstraint.getValue());
                //the offending property
                assertTrue( "author.lastName".equals(invalidConstraint.getPropertyPath()));
            }
        }
    }

    public void test3() {
        Validator bookValidator = getValidator(Book.class);

        assertTrue( bookValidator.hasConstraints());
        ElementDescriptor bookBeanDescriptor = bookValidator.getBeanConstraints();
        assertTrue( bookBeanDescriptor.getElementType() == ElementType.TYPE);
        assertTrue( bookBeanDescriptor.getConstraintDescriptors().size() == 0); //no constraint
        assertTrue( "".equals(bookBeanDescriptor.getPropertyPath())); //root element
        //more specifically "author" and "title"
        assertTrue( bookValidator.getValidatedProperties().size() == 3);
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
        assertTrue(constraintDescriptor.getConstraintImplementation() instanceof StandardConstraint);
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
