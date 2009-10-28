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
import javax.validation.UnexpectedTypeException;
import javax.validation.ValidationException;
import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;

/**
 * Description: validate that number-value of passed object is <= max-value<br/>
 * 
 */
public class DecimalMaxValidator implements ConstraintValidator<DecimalMax, Object> {
    private BigDecimal max;

    public void initialize(DecimalMax constraintAnnotation) {
        try {
            max = new BigDecimal(constraintAnnotation.value());
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
        } else if(value instanceof BigDecimal) {
            bigDec = (BigDecimal)value;
        } else if (value instanceof Number) {
            bigDec = new BigDecimal(((Number)value).doubleValue());
        } else {
            throw new UnexpectedTypeException(value + " is of unexpected type");
        }
        return bigDec != null && bigDec.compareTo(max) != 1;

    }

    private BigDecimal toBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}