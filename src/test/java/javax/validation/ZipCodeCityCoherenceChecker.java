package javax.validation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Documented
@ConstraintValidator(ZipCodeCityCoherenceConstraint.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ZipCodeCityCoherenceChecker {
    String message() default "{beancheck.zipCodeCityCoherence}";
}
