package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <pre>
 * This class is NOT part of the bean_validation spec and might disappear
 * as soon as a final version of the specification contains a similar functionality.
 * </pre>
 */
public class PatternConstraintValidator implements ConstraintValidator<Pattern> {
    private java.util.regex.Pattern pattern;

    public void initialize(Pattern params) {
        pattern = java.util.regex.Pattern.compile(params.regex(), params.flags());
    }

    public boolean isValid(Object ovalue, ConstraintValidatorContext context) {
        if (ovalue == null) return true;
        if (!(ovalue instanceof String)) return false;
        final String value = (String) ovalue;
        return pattern.matcher(value).matches();
    }
}
