package com.agimatec.validation.jsr303.extensions;

import com.agimatec.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Description: class with annotated methods to demonstrate
 * method-level-validation<br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 10:05:12 <br/>
 * Copyright: Agimatec GmbH
 */
public class ExampleMethodService {
    public ExampleMethodService() {
    }

    public ExampleMethodService(@NotNull @NotEmpty String s1, @NotNull String s2) {
    }

    @NotNull
    @NotEmpty
    public String concat(@NotNull @NotEmpty String s1, @NotNull String s2) {
        return s1 + s2;
    }
}
