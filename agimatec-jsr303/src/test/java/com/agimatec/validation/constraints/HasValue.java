package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Description: allow distinct string values for element (like enums) <br/>
 * User: roman <br/>
 * Date: 29.10.2009 <br/>
 * Time: 14:28:43 <br/>
 * Copyright: Agimatec GmbH
 */
@Target({ANNOTATION_TYPE, METHOD, FIELD})
@Constraint(validatedBy = {HasStringValidator.class})
@Retention(RUNTIME)
public @interface HasValue {
    String[] value();

    String message() default "Wrong value, must be one of {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };
}
