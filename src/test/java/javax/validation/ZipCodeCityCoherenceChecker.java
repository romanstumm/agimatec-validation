package javax.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.*;

@Documented
@ConstraintValidator(ZipCodeCityCoherenceConstraint.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ZipCodeCityCoherenceChecker {
}
