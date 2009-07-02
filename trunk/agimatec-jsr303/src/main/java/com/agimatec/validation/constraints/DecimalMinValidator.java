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

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import javax.validation.constraints.DecimalMin;

/**
 * Description: validate that number-value of passed object is >= min-value<br/>
 * 
 * @version $Rev$ $Date$
 */
public class DecimalMinValidator implements ConstraintValidator<DecimalMin, Object> {
    private BigDecimal min;

    public void initialize(DecimalMin constraintAnnotation) {
        try {
            min = new BigDecimal(constraintAnnotation.value());
        } catch (NumberFormatException nfe) {
            throw new ValidationException("Value must compatiable with BigDecimal");
        }
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        BigDecimal bigDec;
        if (value instanceof String) {
            bigDec = toBigDecimal((String) value);
        } else if (value instanceof Number) {
            bigDec = (BigDecimal) value;
        } else {
            throw new IllegalArgumentException("Object must compatiable with BigDecimal");
        }
        if (bigDec == null)
            return false;

        return bigDec.compareTo(min) != -1;
    }

    private BigDecimal toBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}
