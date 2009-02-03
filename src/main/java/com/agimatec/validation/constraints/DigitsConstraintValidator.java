package com.agimatec.validation.constraints;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Digits;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Description: validate that the value is be a number within accepted range.
 * Supported types are:
 * <ul>
 * <li><code>BigDecimal</code></li>
 * <li><code>BigInteger</code></li>
 * <li><code>Number</code></li>
 * <li><code>String</code></li>
 * <li><code>short</code>, <code>int</code>, <code>long</code>, <code>float</code>,
 * <code>double</code></li><br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:32 <br/>
 * Copyright: Agimatec GmbH
 */
public class DigitsConstraintValidator implements ConstraintValidator<Digits, Object> {

    /** maximum number of integral digits accepted for this number */
    private int integral;

    /** maximum number of fractional digits accepted for this number */
    private int fractional;

    public void initialize(Digits constraintAnnotation) {
        integral = constraintAnnotation.integer();
        fractional = constraintAnnotation.fraction();
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public void setFractional(int fractional) {
        this.fractional = fractional;
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext constraintValidatorContext) {

        if (value == null) return true;
        String svalue = null;
        // enhancement: implementation needs improvement!
        if (value instanceof Number) {
            DecimalFormat format = createPlainDigitFormat();
            svalue = format.format(value);
        } else if (value instanceof String) {
            svalue = (String)value;
        }
        if(svalue != null) {
            int idx = svalue.indexOf('.');
            String left, right;
            if(idx < 0) {
                // no faction
                left = svalue;
                right = "";
            } else {
                left = svalue.substring(0, idx);
                right = svalue.substring(idx+1);
            }
            return ((left.length() == 0 || StringUtils.isNumeric(left)) &&
                   left.length()<=integral) &&
                  ((right.length() == 0 || StringUtils.isNumeric(right)) &&
                   right.length()<=fractional);
        }
        return false;
    }

    private DecimalFormat createPlainDigitFormat() {
        DecimalFormat plainNumericFormat = new DecimalFormat("#.#");
        plainNumericFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
        plainNumericFormat.setMaximumIntegerDigits(Integer.MAX_VALUE);
        plainNumericFormat.setGroupingUsed(false);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        plainNumericFormat.setDecimalFormatSymbols(dfs);
        return plainNumericFormat;
    }
}
