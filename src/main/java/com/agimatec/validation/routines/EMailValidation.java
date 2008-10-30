package com.agimatec.validation.routines;

import com.agimatec.validation.Validation;
import com.agimatec.validation.ValidationContext;

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
    private static java.util.regex.Pattern pattern;
    private java.util.regex.Pattern customPattern = pattern;

    static {
        pattern = java.util.regex.Pattern.compile(
                "^" + ATOM + "+(\\." + ATOM + "+)*@"
                        + DOMAIN
                        + "|"
                        + IP_DOMAIN
                        + ")$",
                java.util.regex.Pattern.CASE_INSENSITIVE
        );
    }

    public void validate(ValidationContext context) {
        if (context.getPropertyValue() == null) return;
        if (!isValid(context.getPropertyValue(), customPattern)) {
            context.getListener().addError(Reasons.EMAIL_ADDRESS, context);
        }
    }

    public Pattern getPattern() {
        return customPattern;
    }

    public void setPattern(Pattern pattern) {
        this.customPattern = pattern;
    }

    public static boolean isValid(Object value) {
        return isValid(value, pattern);
    }

    private static boolean isValid(Object value, Pattern aPattern) {
        if (value == null) return true;
        if (!(value instanceof String)) return false;
        String string = (String) value;
        if (string.length() == 0) return true;
        Matcher m = aPattern.matcher(string);
        return m.matches();
    }

}
