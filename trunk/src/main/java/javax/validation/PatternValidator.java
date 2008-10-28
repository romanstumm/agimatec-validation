package javax.validation;

/**
 * <pre>--
 * As soon as the classes in javax.validation are available from official sites, this
 * class will be removed from this compilation unit.
 * --</pre>
 */
public class PatternValidator implements Constraint<Pattern> {
    private java.util.regex.Pattern pattern;

    public void initialize(Pattern params) {
        pattern = java.util.regex.Pattern.compile(params.regex(), params.flags());
    }

    public boolean isValid(Object ovalue, Context context) {
        if (ovalue == null) return true;
        if (!(ovalue instanceof String)) return false;
        final String value = (String) ovalue;
        return pattern.matcher(value).matches();
    }
}
