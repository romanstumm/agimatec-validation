package javax.validation;

import java.util.regex.Matcher;

/**
 * <p>
 * --
 * This class is NOT part of the bean_validation spec and might change
 * as soon as a final version of the specification is available.
 * --
 * <p/>
 * Description: implementation taken from hibernate.<br/>
 * User: roman.stumm <br/>
 * Date: 14.10.2008 <br/>
 * Time: 12:38:37 <br/>
 * Copyright: Agimatec GmbH
 */
public class EmailValidator implements Constraint<Email> {
    private static String ATOM =
            "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
    private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
    private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    private java.util.regex.Pattern pattern;

    public boolean isValid(Object value) {
        if (value == null) return true;
        if (!(value instanceof String)) return false;
        String string = (String) value;
        if (string.length() == 0) return true;
        Matcher m = pattern.matcher(string);
        return m.matches();
    }

    public void initialize(Email parameters) {
        pattern = java.util.regex.Pattern.compile(
                "^" + ATOM + "+(\\." + ATOM + "+)*@"
                        + DOMAIN
                        + "|"
                        + IP_DOMAIN
                        + ")$",
                java.util.regex.Pattern.CASE_INSENSITIVE
        );
    }

}
