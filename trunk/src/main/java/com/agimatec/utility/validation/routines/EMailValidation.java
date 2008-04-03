package com.agimatec.utility.validation.routines;

import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.ValidationListener;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Description: example validation for email addresses using a regular expression<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 16:51:16 <br/>
 *
 */
public class EMailValidation implements Validation {
    private static final String EMail_RegExp =
            "([a-zA-Z\\d]((\\w|-|\\.(?!\\.))*\\w)?@(([a-zA-Z]|(\\w(\\w|-(?!-))*\\w))\\.)" +
                    "+[a-zA-Z]{2,})|([a-zA-Z\\d][a-zA-Z\\d ]* <[a-zA-Z\\d]((\\w|-|\\.(?!\\.))*\\w)" +
                    "?@(([a-zA-Z]|(\\w(\\w|-(?!-))*\\w))\\.)+[a-zA-Z]{2,}>)";
    private static final Pattern pattern = Pattern.compile(EMail_RegExp);

    public void validate(ValidationContext context, ValidationListener listener) {
        if (context.getPropertyValue() == null) return;
        try {
            if (!pattern.matcher((String) context.getPropertyValue()).matches()) {
                listener.addError(Reasons.EMAIL_ADDRESS, context);
            }
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("email expression malformed at " + context,
                    e);
        }
    }

    public static boolean isValid(String emailaddress)
    {
        return !StringUtils.isEmpty(emailaddress) &&
                pattern.matcher(emailaddress).matches();
    }
}
