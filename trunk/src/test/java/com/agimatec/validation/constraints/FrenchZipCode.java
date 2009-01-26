package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.OverridesParameter;
import javax.validation.OverridesParameters;
import javax.validation.ReportAsViolationFromCompositeConstraint;
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
@Length(min = 4, max = 5, message = "Zipcode should be of size {value}")
@Constraint(validatedBy = FrenchZipcodeValidator.class)
@ReportAsViolationFromCompositeConstraint
@Documented
@Target({ANNOTATION_TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface FrenchZipCode {
    @OverridesParameters({
        @OverridesParameter(constraint = Length.class, parameter = "min"),
        @OverridesParameter(constraint = Length.class, parameter = "max")})
    int size() default 6;

    @OverridesParameter(constraint=Length.class, parameter="message")
    String sizeMessage() default "{error.zipcode.size}";

    String message() default "Wrong zipcode";

    String[] groups() default {};
}
