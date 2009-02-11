package com.agimatec.validation.constraints;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 12:02:06 <br/>
 */
@NotEmpty
@NotNull
@Size(min = 4, max = 5)
@Retention(RUNTIME)
public @interface Password {
    String[] groups() default {};

    int robustness() default 8;
}
