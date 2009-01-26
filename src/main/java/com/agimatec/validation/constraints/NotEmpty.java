package com.agimatec.validation.constraints;

import javax.validation.Constraint;
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
@Constraint(validatedBy = NotEmptyConstraintValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface NotEmpty {
    Class<?>[] groups() default {};

    String message() default "{validator.notEmpty}";
}
