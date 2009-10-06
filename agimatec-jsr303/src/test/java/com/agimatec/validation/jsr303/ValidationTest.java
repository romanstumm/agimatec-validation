/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package com.agimatec.validation.jsr303;

import com.agimatec.validation.constraints.NotNullValidator;
import com.agimatec.validation.jsr303.example.*;
import com.agimatec.validation.jsr303.util.TestUtils;
import junit.framework.Assert;
import junit.framework.TestCase;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
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
    }

    public void testBook() {
        Validator validator = getValidator();
        Author author = new Author();
        author.setLastName("Baudelaire");
        author.setFirstName("");
        Book book = new Book();
        book.setAuthor(author);
        book.setSubtitle("12345678900125678901234578901234567890");

        // NotEmpty failure on the title field
        Set<ConstraintViolation<Book>> errors = validator.validate(book, Book.All.class);
        Assert.assertTrue(!errors.isEmpty());

        book.setTitle("Les fleurs du mal");
        author.setCompany("Some random publisher with a very very very long name");

        // author.firstName fails to pass the NotEmpty constraint
        //  author.company fails to pass the Size constraint
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

        Validator v = getValidator();
        Set found = v.validate(a, Default.class, First.class, Last.class);
        Assert.assertTrue(!found.isEmpty());
        Assert.assertEquals(4, found.size());

        adr.setCity("Berlin");
        adr.setZipCode("12345");
        adr.setCompany("agimatec GmbH");
        found = v.validate(a, Default.class, First.class, Last.class);
        Assert.assertEquals(1, found.size());
        ConstraintViolation ic = (ConstraintViolation) found.iterator().next();
        Assert.assertEquals("addresses[0].country.name", ic.getPropertyPath().toString());
    }

    public void testPropertyPathWithIndex() {
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

        Set<ConstraintViolation<Author>> constraints = getValidator().validate(a);
        Assert.assertTrue(!constraints.isEmpty());

        assertPropertyPath("addresses[0].country", constraints);
        assertPropertyPath("addresses[1].country", constraints);
        assertPropertyPath("addresses[2].country", constraints);
    }

    private <T> void assertPropertyPath(String propertyPath,
                                        Set<ConstraintViolation<T>> constraints) {
        for (ConstraintViolation each : constraints) {
            if (each.getPropertyPath().toString().equals(propertyPath)) return;
        }
        Assert.fail(propertyPath + " not found in " + constraints);
    }

    public void testPropertyPathRecursive() {
        RecursiveFoo foo1 = new RecursiveFoo();
        RecursiveFoo foo11 = new RecursiveFoo();
        foo1.getFoos().add(foo11);
        RecursiveFoo foo12 = new RecursiveFoo();
        foo1.getFoos().add(foo12);
        RecursiveFoo foo2 = new RecursiveFoo();
        foo11.getFoos().add(foo2);

        Set<ConstraintViolation<RecursiveFoo>> constraints =
              getValidator().validate(foo1);
        assertPropertyPath("foos[0].foos[0].foos", constraints);
        assertPropertyPath("foos[1].foos", constraints);
    }

    public void testNullElementInCollection() {
        try {
            getValidator().validate(null);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }
        RecursiveFoo foo = new RecursiveFoo();
        foo.getFoos().add(new RecursiveFoo());
        foo.getFoos().add(null);
        Assert.assertTrue(!getValidator().validate(foo).isEmpty());
        // check that no nullpointer exception gets thrown
    }

    private Validator getValidator() {
        return AgimatecValidatorFactory.getDefault().getValidator();
    }

    public void testGroups() {
        Validator validator = getValidator();
        Author author = new Author();
        author.setCompany("ACME");
        Book book = new Book();
        book.setTitle("");
        book.setAuthor(author);
        boolean foundTitleConstraint = false;
        Set<ConstraintViolation<Book>> constraintViolations =
              validator.validate(book, Book.All.class);
        assertEquals(1, constraintViolations.size());
        //assuming an english locale, the interpolated message is returned
        for (ConstraintViolation constraintViolation : constraintViolations) {
            if (constraintViolation.getRootBean().getClass() == Book.class) {
                Assert.assertTrue(
                      "may not be empty".equals(constraintViolation.getMessage()));
                Assert.assertTrue(book == constraintViolation.getRootBean());

                //the offending property
                if (constraintViolation.getPropertyPath().toString().equals("title")) {
                    foundTitleConstraint = true;
                    //the offending value
                    Assert.assertEquals(book.getTitle(),
                          constraintViolation.getInvalidValue());
                }
            }
        }
        Assert.assertTrue(foundTitleConstraint);
    }

    /**
     * Example 2.14. Using the fluent API to build custom constraint violations.
     * test that:
     * the {@link com.agimatec.validation.constraints.ZipCodeCityCoherenceValidator} adds
     * custom messages to the context and suppresses the default message
     */
    public void testConstraintValidatorContextFluentAPI() {
        Address ad = new Address();
        ad.setCity("error");
        ad.setZipCode("error");
        ad.setAddressline1("something");
        ad.setCountry(new Country());
        ad.getCountry().setName("something");
        Validator v = getValidator();
        Set<ConstraintViolation<Address>> violations = v.validate(ad);
        Assert.assertEquals(2, violations.size());
        for (ConstraintViolation each : violations) {
            Assert.assertTrue(each.getMessage().endsWith(" not OK"));
        }
        assertNotNull(TestUtils.getViolation(violations, "city"));
        assertNotNull(TestUtils.getViolation(violations, ""));
    }

    public void testValidateNestedPropertyPath() throws InvocationTargetException,
          NoSuchMethodException, IllegalAccessException {
        final String propPath = "addresses[0].country.ISO2Code";

        Validator v = getValidator();
        Author author = new Author();
        author.setAddresses(new ArrayList());
        Address adr = new Address();
        author.getAddresses().add(adr);
        Country country = new Country();
        adr.setCountry(country);
        country.setISO2Code("too_long");

        Set<ConstraintViolation<Author>> iv = v.validateProperty(author, propPath);
        Assert.assertEquals(1, iv.size());
        country.setISO2Code("23");
        iv = v.validateProperty(author, propPath);
        Assert.assertEquals(0, iv.size());
        iv = v.validateValue(Author.class, propPath, "345");
        Assert.assertEquals(1, iv.size());
        iv = v.validateValue(Author.class, propPath, "34");
        Assert.assertEquals(0, iv.size());
    }

    public void testMetadataAPI() {
        Validator bookValidator = getValidator();
        BeanDescriptor bookBeanDescriptor =
              bookValidator.getConstraintsForClass(Book.class);
//
        Assert.assertTrue(bookBeanDescriptor.hasConstraints());
//          assertTrue(bookBeanDescriptor.getElementType() == ElementType.TYPE);
        Assert.assertTrue(
              bookBeanDescriptor.getConstraintDescriptors().size() == 0); //no constraint
//        assertTrue("".equals(bookBeanDescriptor.getPropertyPath())); //root element
        //more specifically "author" and "title"
        Assert.assertTrue(bookBeanDescriptor.getConstrainedProperties().size() == 3);
        //not a property
        Assert.assertTrue(
              bookBeanDescriptor.getConstraintsForProperty("doesNotExist") == null);
        //property with no constraint
        Assert.assertTrue(
              bookBeanDescriptor.getConstraintsForProperty("description") == null);
        PropertyDescriptor propertyDescriptor =
              bookBeanDescriptor.getConstraintsForProperty("title");
//        assertTrue(propertyDescriptor.getElementType() == ElementType.METHOD);
        Assert.assertEquals(2, propertyDescriptor.getConstraintDescriptors().size());
        Assert.assertTrue("title".equals(propertyDescriptor.getPropertyName()));
        //assuming the implementation returns the NotEmpty constraint first
        Iterator<ConstraintDescriptor<?>> iter =
              propertyDescriptor.getConstraintDescriptors().iterator();
        ConstraintDescriptor constraintDescriptor = null;
        while (iter.hasNext()) {
            constraintDescriptor = iter.next();
            if (constraintDescriptor.getAnnotation().annotationType()
                  .equals(NotNull.class)) {
                break;
            }

        }
        Assert.assertTrue(constraintDescriptor != null);
        Assert.assertTrue(constraintDescriptor.getGroups().size() == 1); //"first"
        Assert.assertEquals(NotNullValidator.class,
              constraintDescriptor.getConstraintValidatorClasses().get(0));
        /*  StandardConstraint standardConstraint =
                (StandardConstraint) ((ConstraintValidation) constraintDescriptor).
                        getConstraintImplementation();
        //@NotEmpty cannot be null
        assertTrue(!standardConstraint.getStandardConstraints().getNullability());*/
        //assuming the implementation returns the Size constraint first
        propertyDescriptor = bookBeanDescriptor.getConstraintsForProperty("subtitle");
        Iterator<ConstraintDescriptor<?>> iterator =
              propertyDescriptor.getConstraintDescriptors().iterator();
        constraintDescriptor = iterator.next();
        Assert.assertTrue(
              constraintDescriptor.getAnnotation().annotationType().equals(Size.class));
        Assert.assertTrue(
              ((Integer) constraintDescriptor.getAttributes().get("max")) == 30);
        Assert.assertTrue(constraintDescriptor.getGroups().size() == 1);
        propertyDescriptor = bookBeanDescriptor.getConstraintsForProperty("author");
//        assertEquals(ElementType.FIELD, propertyDescriptor.getElementType());
        Assert.assertTrue(propertyDescriptor.getConstraintDescriptors().size() == 1);
        Assert.assertTrue(propertyDescriptor.isCascaded());
    }
}
