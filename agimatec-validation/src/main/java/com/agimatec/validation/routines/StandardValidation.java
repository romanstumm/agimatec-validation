/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.agimatec.validation.routines;

import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.Validation;
import com.agimatec.validation.model.ValidationContext;
import static com.agimatec.validation.routines.Reasons.*;
import com.agimatec.validation.xml.XMLMetaValue;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Description: This class implements the standard validations for properties!
 * You can subclass this class and replace the implementation
 * in the beanInfo-xml by providing it a validation "standard"<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 12:41:06 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class StandardValidation implements Validation {
    /** key for this validation in the validation list of the beanInfos */
    public String getValidationId() {
        return "standard";
    }

    public void validate(ValidationContext context) {
        validateMandatory(context);
        validateMaxLength(context);
        validateMinLength(context);
        validateMaxValue(context);
        validateMinValue(context);
        validateRegExp(context);
        validateTimeLag(context);
    }

    protected void validateTimeLag(ValidationContext context) {
        if (context.getPropertyValue() == null) return;

        String lag = (String) context.getMetaProperty().getFeature(TIME_LAG);
        if (lag == null) return;
        long date = ((Date) context.getPropertyValue()).getTime();
        long now = System.currentTimeMillis();
        if (XMLMetaValue.TIMELAG_Future.equals(lag)) {
            if (date < now) {
                context.getListener().addError(TIME_LAG, context);
            }
        } else if (XMLMetaValue.TIMELAG_Past.equals(lag)) {
            if (date > now) {
                context.getListener().addError(TIME_LAG, context);
            }
        } else {
            throw new IllegalArgumentException("unknown timelag " + lag + " at " + context);
        }
    }

    private static final String REG_EXP_PATTERN = "cachedRegExpPattern";

    protected void validateRegExp(ValidationContext context) {
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
                context.getListener().addError(REG_EXP, context);
            }
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(
                    "regular expression malformed. regexp " + regExp + " at " + context, e);
        }
    }

    protected void validateMinValue(ValidationContext context) {
        if (context.getPropertyValue() == null) return;
        Comparable minValue = (Comparable) context.getMetaProperty().getFeature(MIN_VALUE);
        if (minValue == null) return;
        int r = minValue.compareTo(context.getPropertyValue());
        if (r > 0) {
            context.getListener().addError(MIN_VALUE, context);
        }
    }

    protected void validateMaxValue(ValidationContext context) {
        if (context.getPropertyValue() == null) return;
        Comparable maxValue = (Comparable) context.getMetaProperty().getFeature(MAX_VALUE);
        if (maxValue == null) return;
        int r = maxValue.compareTo(context.getPropertyValue());
        if (r < 0) {
            context.getListener().addError(MAX_VALUE, context);
        }
    }

    protected void validateMaxLength(ValidationContext context) {
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
            context.getListener().addError(MAX_LENGTH, context);
        }
    }

    protected void validateMinLength(ValidationContext context) {
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
            context.getListener().addError(MIN_LENGTH, context);
        }
    }

    protected void validateMandatory(ValidationContext context) {
        if (context.getMetaProperty().isMandatory()) {
            if (context.getPropertyValue() == null) {
                context.getListener().addError(MANDATORY, context);
            }
        }
    }

    public static StandardValidation getInstance() {
        return new StandardValidation();
    }
}
