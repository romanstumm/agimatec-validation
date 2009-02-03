package com.agimatec.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Annotate an element (parameter) to be validated.
 * <br>
 *
 * Wichtig:<br>
 * Die Methode muss ebenfalls mit Validate annotiert werden, damit
 * die Parameter-Annotations ueberhaupt untersucht und ein BeanValidationContext angelegt wird.
 * <br/>
 * User: roman.stumm <br/>
 * Date: 09.07.2007 <br/>
 * Time: 13:38:24 <br/>
 * Copyright: Agimatec GmbH 2008
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Validate {
    /** (optional) the MetaBean.id to use */
    String value() default "";
}
