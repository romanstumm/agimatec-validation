package com.agimatec.validation.jsr303.example;

import com.agimatec.validation.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.09.2008 <br/>
 * Time: 11:02:24 <br/>
 * Copyright: Agimatec GmbH
 */
public class RecursiveFoo {
    @NotEmpty
    @Valid
    Collection<RecursiveFoo> foos = new ArrayList();

    public Collection<RecursiveFoo> getFoos() {
        return foos;
    }

    public void setFoos(Collection<RecursiveFoo> foos) {
        this.foos = foos;
    }
}
