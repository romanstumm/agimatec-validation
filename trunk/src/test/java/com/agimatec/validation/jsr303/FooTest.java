package com.agimatec.validation.jsr303;

import com.agimatec.validation.constraints.NotNull;
import junit.framework.TestCase;

import javax.validation.InvalidConstraint;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 08.09.2008 <br/>
 * Time: 13:44:58 <br/>
 * Copyright: Agimatec GmbH
 */
public class FooTest extends TestCase {

    @Valid
    private Collection<Foo> foos = new ArrayList<Foo>();

    public FooTest() {
        foos.add(new Foo("foo1"));
        foos.add(null);
        foos.add(new Foo("foo3"));
    }


    public class Foo {
        @NotNull
        public String bar;

        public Foo(String bar) {
            this.bar = bar;
        }

    }

    public void testValidation() {
        FooTest t = new FooTest();

        ClassValidator v = new ClassValidator(t.getClass());
        Set<InvalidConstraint> errors = v.validate(t);
        System.out.println("got errors:");
        for (InvalidConstraint error : errors) {
            System.out.println(error.getPropertyPath());
        }
    }
}