package com.agimatec.utility.validation.routines;

import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.ValidationListener;
import com.agimatec.utility.validation.model.Features;
import com.agimatec.utility.validation.model.MetaProperty;
import static com.agimatec.utility.validation.routines.Reasons.*;
import com.agimatec.utility.validation.xml.XMLMetaValue;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Description: This class implements the standard validations.
 * You can subclass this class and replace the implementation
 * in the beanInfo-xml by providing it a validation "standard"<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 12:41:06 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class StandardValidation implements Validation {
    /**
     * key for this validation in the validation list of the beanInfos
     */
    public String getValidationId() {
        return "standard";
    }

    public void validate(ValidationContext context, ValidationListener listener) {
        validateMandatory(context, listener);
        validateMaxLength(context, listener);
        validateMinLength(context, listener);
        validateMaxValue(context, listener);
        validateMinValue(context, listener);
        validateRegExp(context, listener);
        validateTimeLag(context, listener);
    }

    protected void validateTimeLag(ValidationContext context,
                                   ValidationListener listener) {
        if (context.getPropertyValue() == null) return;

        String lag = (String) context.getMetaProperty().getFeature(TIME_LAG);
        if (lag == null) return;
        long date = ((Date) context.getPropertyValue()).getTime();
        long now = System.currentTimeMillis();
        if (XMLMetaValue.TIMELAG_Future.equals(lag)) {
            if (date < now) {
                listener.addError(TIME_LAG, context);
            }
        } else if (XMLMetaValue.TIMELAG_Past.equals(lag)) {
            if (date > now) {
                listener.addError(TIME_LAG, context);
            }
        } else {
            throw new IllegalArgumentException(
                    "unknown timelag " + lag + " at " + context);
        }
    }

    private static final String REG_EXP_PATTERN = "cachedRegExpPattern";

    protected void validateRegExp(ValidationContext context,
                                  ValidationListener listener) {
        if (context.getPropertyValue() == null) return;

        final MetaProperty meta = context.getMetaProperty();
        final String regExp = (String) meta.getFeature(REG_EXP);
        if (regExp == null) return;

        final String value = String.valueOf(context.getPropertyValue());
        try {
            Pattern pattern = (Pattern) meta.getFeature(REG_EXP_PATTERN);
            if (pattern == null) {
                pattern = Pattern.compile(regExp);
                meta.putFeature(REG_EXP_PATTERN, pattern);
            }
            if (!pattern.matcher(value).matches()) {
                listener.addError(REG_EXP, context);
            }
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(
                    "regular expression malformed. regexp " + regExp + " at " + context,
                    e);
        }
    }

    protected void validateMinValue(ValidationContext context,
                                    ValidationListener listener) {
        if (context.getPropertyValue() == null) return;
        Comparable minValue =
                (Comparable) context.getMetaProperty().getFeature(MIN_VALUE);
        if (minValue == null) return;
        int r = minValue.compareTo(context.getPropertyValue());
        if (r > 0) {
            listener.addError(MIN_VALUE, context);
        }
    }

    protected void validateMaxValue(ValidationContext context,
                                    ValidationListener listener) {
        if (context.getPropertyValue() == null) return;
        Comparable maxValue =
                (Comparable) context.getMetaProperty().getFeature(MAX_VALUE);
        if (maxValue == null) return;
        int r = maxValue.compareTo(context.getPropertyValue());
        if (r < 0) {
            listener.addError(MAX_VALUE, context);
        }
    }

    protected void validateMaxLength(ValidationContext context,
                                     ValidationListener listener) {
        if (context.getPropertyValue() == null) return;
        Integer maxLength = (Integer) context.getMetaProperty()
                .getFeature(Features.Property.MAX_LENGTH);
        if (maxLength == null) return;

        final Object value = context.getPropertyValue();
        int length = 0;
        if (value instanceof String) {
            length = ((String) value).length();
        } else if (value instanceof Collection) {
            length = ((Collection) value).size();
        }
        if (length > maxLength) {
            listener.addError(MAX_LENGTH, context);
        }
    }

    protected void validateMinLength(ValidationContext context,
                                     ValidationListener listener) {
        if (context.getPropertyValue() == null) return;
        Integer maxLength = (Integer) context.getMetaProperty()
                .getFeature(Features.Property.MIN_LENGTH);
        if (maxLength == null) return;

        final Object value = context.getPropertyValue();
        int length = 0;
        if (value instanceof String) {
            length = ((String) value).length();
        } else if (value instanceof Collection) {
            length = ((Collection) value).size();
        }
        if (length < maxLength) {
            listener.addError(MIN_LENGTH, context);
        }
    }

    protected void validateMandatory(ValidationContext context,
                                     ValidationListener listener) {
        if (context.getMetaProperty().isMandatory()) {
            if (context.getPropertyValue() == null) {
                listener.addError(MANDATORY, context);
            }
        }
    }

    public static StandardValidation getInstance() {
        return new StandardValidation();
    }
}
