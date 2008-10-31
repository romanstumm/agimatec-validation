package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 */
@Documented
@ConstraintValidator(MinConstraint.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Min {
    String[] groups() default {};

    int value();

    String message() default "{validator.min}";
}