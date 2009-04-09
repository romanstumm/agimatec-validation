package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * Description: validate that the value is be a number within accepted range.
 * Supported types are:
 * <ul>
 * <li><code>BigDecimal</code></li>
 * <li><code>BigInteger</code></li>
 * <li><code>Number</code></li>
 * <li><code>String</code></li>
 * <li><code>byte</code>,<code>short</code>, <code>int</code>, <code>long</code>, <code>float</code>,
 * <code>double</code></li><br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:32 <br/>
 * Copyright: Agimatec GmbH
 */
public class DigitsValidator implements ConstraintValidator<Digits, Object> {

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

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        BigDecimal bigInt;
        if (value instanceof String) {
            bigInt = toBigDecimal((String) value);
        } else if (value instanceof BigDecimal) {
            bigInt = (BigDecimal) value;
        } else {
            try {
//            DecimalFormat format = createPlainDigitFormat();
//            value = format.format(value);
                bigInt = new BigDecimal(value.toString());
            } catch (NumberFormatException ex) {
                bigInt = null;
            }
        }
        if (bigInt == null) return false;

        int intLen = bigInt.precision() - bigInt.scale();
        if(integral >= intLen) {
            int factLen = bigInt.scale() < 0 ? 0 : bigInt.scale();
            return fractional >= factLen;
        } else {
            return false;
        }
    }

    private BigDecimal toBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    /*protected DecimalFormat createPlainDigitFormat() {
        DecimalFormat plainNumericFormat = new DecimalFormat("#.#");
        plainNumericFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
        plainNumericFormat.setMaximumIntegerDigits(Integer.MAX_VALUE);
        plainNumericFormat.setGroupingUsed(false);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        plainNumericFormat.setDecimalFormatSymbols(dfs);
        return plainNumericFormat;
    }*/
}
