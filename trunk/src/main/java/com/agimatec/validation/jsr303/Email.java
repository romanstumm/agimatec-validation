package com.agimatec.validation.jsr303;

import javax.validation.ConstraintValidator;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * <p>
 * --
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * --
 * </p>
 * Description: annotation to validate an email address (by pattern)<br/>
 * User: roman.stumm <br/>
 * Date: 14.10.2008 <br/>
 * Time: 12:38:10 <br/>
 * Copyright: Agimatec GmbH
 */
@Documented
@ConstraintValidator(EmailValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Email {
}