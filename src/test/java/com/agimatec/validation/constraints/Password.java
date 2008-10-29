package com.agimatec.validation.constraints;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 12:02:06 <br/>
 *
 */
//TODO RSt - @ConstraintValidator(PasswordConstraint.class)
@Retention(RUNTIME)
public @interface Password {
    String[] groups() default {};

    int robustness() default 8;
}
