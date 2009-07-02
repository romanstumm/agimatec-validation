/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package com.agimatec.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
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
        if (integral < 0)
            throw new ValidationException("Integer cannot be negative");
        if (fractional < 0)
            throw new ValidationException( "Fraction cannot be negative" );
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
