package javax.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Description: this type will be removed from this compilation unit as soon
 * as the validation-api jar contains it!<br/>
 * User: roman.stumm <br/>
 * Date: 31.10.2008 <br/>
 * Time: 16:41:00 <br/>
 * Copyright: Agimatec GmbH
 */
@Documented
@Retention(RUNTIME)
public @interface OverridesParameters {
    OverridesParameter[] value();
}
