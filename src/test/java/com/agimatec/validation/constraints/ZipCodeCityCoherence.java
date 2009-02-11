package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ZipCodeCityCoherenceValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ZipCodeCityCoherence {
    String message() default "{validator.zipCodeCityCoherence}";
}
