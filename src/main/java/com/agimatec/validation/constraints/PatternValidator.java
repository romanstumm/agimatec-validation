package com.agimatec.validation.constraints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

/**
 * validator using a regular expression,
 * based on the jsr303 Pattern constraint annotation.
 */
public class PatternValidator implements ConstraintValidator<Pattern, String> {
    private static final Log log = LogFactory.getLog(PatternValidator.class);

    protected java.util.regex.Pattern pattern;

    public void initialize(Pattern params) {
        pattern = java.util.regex.Pattern.compile(params.regexp(), toInt(params.flags()));
    }

    private int toInt(Pattern.Flag[] flags) {
        int value = 0;
        for (Pattern.Flag flag : flags) {
            value |= toInt(flag);
        }
        return value;
    }

    private int toInt(Pattern.Flag flag) {
        switch (flag) {
            case UNIX_LINES:
                return java.util.regex.Pattern.UNIX_LINES;
            case CASE_INSENSITIVE:
                return java.util.regex.Pattern.CASE_INSENSITIVE;
            case COMMENTS:
                return java.util.regex.Pattern.COMMENTS;
            case MULTILINE:
                return java.util.regex.Pattern.MULTILINE;
            case DOTALL:
                return java.util.regex.Pattern.DOTALL;
            case UNICODE_CASE:
                return java.util.regex.Pattern.UNICODE_CASE;
            case CANON_EQ:
                return java.util.regex.Pattern.CANON_EQ;
            default:
                try {
                    return ((Integer) java.util.regex.Pattern.class.getField(flag.name())
                          .get(null)).intValue();
                } catch (Exception e) {
                    log.error("flag not supported: " + flag.name());
                    return 0;
                }
        }
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || pattern.matcher(value).matches();
    }
}
