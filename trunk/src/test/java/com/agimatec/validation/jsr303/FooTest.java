package com.agimatec.validation.jsr303;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 08.09.2008 <br/>
 * Time: 13:44:58 <br/>
 * Copyright: Agimatec GmbH
 */

import junit.framework.TestCase;

import javax.validation.InvalidConstraint;
import javax.validation.NotNull;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ddalessa
 * Date: Sep 8, 2008
 * Time: 9:34:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class FooTest extends TestCase
{

    @Valid
    private Collection<Foo> foos = new ArrayList<Foo>();

    public FooTest()
    {
        foos.add(new Foo("foo1"));
        foos.add(null);
        foos.add(new Foo("foo3"));
    }


    public class Foo
    {
        @NotNull
        public String bar;

        public Foo(String bar)
        {
            this.bar = bar;
        }

    }

    public void testValidation()
    {
        FooTest t = new FooTest();

        GroupBeanValidationContext.INCLUDE_INDEX_IN_PROPERTY_PATH = true;

        ClassValidator v = new ClassValidator(t.getClass());
        Set<InvalidConstraint> errors = v.validate(t);
        System.out.println("got errors:");
        for (InvalidConstraint error : errors)
        {
            System.out.println(error.getPropertyPath());
        }
    }
}