package com.agimatec.validation.constraints;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Pattern.List({
    // email
    @Pattern(regexp = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}"),
    // agimatec
    @Pattern(regexp = ".*?agimatec.*?")
})
@Constraint(validatedBy = {})
@Documented
@Target({ANNOTATION_TYPE, METHOD, FIELD, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface AgimatecEmail {
    String message() default "Not an agimatec email";

    @OverridesAttribute(constraint = Pattern.class, name = "message",
          constraintIndex = 0) String emailMessage() default "Not an email";

    @OverridesAttribute(constraint = Pattern.class, name = "message",
          constraintIndex = 1) String agimatecMessage() default "Not Agimatec";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
          @interface List {
        AgimatecEmail[] value();
    }
}