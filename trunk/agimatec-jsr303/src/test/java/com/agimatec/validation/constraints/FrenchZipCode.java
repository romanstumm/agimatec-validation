package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Description: example for composed constraint <br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 16:34:56 <br/>
 * Copyright: Agimatec GmbH
 */
@NotEmpty
@NotNull
@Size(min = 4, max = 5, message = "Zipcode should be of size {value}")
@Constraint(validatedBy = FrenchZipcodeValidator.class)
@ReportAsSingleViolation
@Documented
@Target({ANNOTATION_TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface FrenchZipCode {
    @OverridesAttribute.List({
            @OverridesAttribute(constraint = Size.class, name= "min"),
        @OverridesAttribute(constraint = Size.class, name = "max")})
    int size() default 6;

    @OverridesAttribute(constraint=Size.class, name="message")
    String sizeMessage() default "{error.zipcode.size}";

    String message() default "Wrong zipcode";

    String[] groups() default {};
}
