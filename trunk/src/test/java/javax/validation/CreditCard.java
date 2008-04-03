package javax.validation;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 12:02:37 <br/>
 *
 */
@ConstraintValidator(CreditCardConstraint.class)
@Retention(RUNTIME)
public @interface CreditCard {
    String[] groups() default {};

    String message() default "{beancheck.creditcard}";
}
