package com.agimatec.utility.validation.routines;

import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.ValidationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: example validation for email addresses using a regular expression
 * (taken from hibernate EmailValidator)<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 16:51:16 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class EMailValidation implements Validation {
    private static String ATOM =
            "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
    private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
    private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
    private java.util.regex.Pattern pattern;

    public void validate(ValidationContext context) {
        if (context.getPropertyValue() == null) return;
        if (!isValid(context.getPropertyValue())) {
            context.getListener().addError(Reasons.EMAIL_ADDRESS, context);
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean isValid(Object value) {
        if (value == null) return true;
        if (!(value instanceof String)) return false;
        String string = (String) value;
        if (string.length() == 0) return true;

        if (pattern == null) {
            pattern = java.util.regex.Pattern.compile(
                    "^" + ATOM + "+(\\." + ATOM + "+)*@"
                            + DOMAIN
                            + "|"
                            + IP_DOMAIN
                            + ")$",
                    java.util.regex.Pattern.CASE_INSENSITIVE
            );
        }

        Matcher m = pattern.matcher(string);
        return m.matches();
    }
}
