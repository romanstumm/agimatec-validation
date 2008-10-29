package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Validate that the string is between 'min' and 'max' (inclusive).
 * 
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 */
@Documented
@ConstraintValidator(LengthConstraint.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Length {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "{validator.length}";

    String[] groups() default {};
}