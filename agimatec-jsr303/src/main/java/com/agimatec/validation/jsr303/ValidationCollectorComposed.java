package com.agimatec.validation.jsr303;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 12:28:56 <br/>
 * Copyright: Agimatec GmbH
 */
public class ValidationCollectorComposed implements ValidationCollector {
    private final AnnotationConstraintBuilder builder;

    public ValidationCollectorComposed(AnnotationConstraintBuilder builder) {
        this.builder = builder;
    }

    public void addValidation(ConstraintValidation validation) {
        builder.addComposed(validation);
    }
}
