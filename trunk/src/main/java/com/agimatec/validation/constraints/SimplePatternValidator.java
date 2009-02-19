package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @deprecated validator using a regular expression,
 * based on the deprecated Pattern constraint annotation by agimatec.
 * User: roman <br/>
 * Date: 19.02.2009 <br/>
 * Time: 10:37:06 <br/>
 * Copyright: Agimatec GmbH
 */
public class SimplePatternValidator implements ConstraintValidator<Pattern, String> {
    private java.util.regex.Pattern pattern;

    public void initialize(Pattern params) {
        pattern = java.util.regex.Pattern.compile(params.regex(), params.flags());
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || pattern.matcher(value).matches();
    }
}
