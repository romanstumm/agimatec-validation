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
import javax.validation.constraints.Max;
import java.math.BigDecimal;

/**
 * Description: validate that number-value of passed object is <= max-value<br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:48:54 <br/>
 * Copyright: Agimatec GmbH
 */
public class MaxValidator implements ConstraintValidator<Max, Object> {
    private long max;

    public void initialize(Max constraintAnnotation) {
        max = constraintAnnotation.value();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue() <= max;
        } else {
            try {
                return new BigDecimal(String.valueOf(value)).longValue() <= max;
            } catch ( NumberFormatException nfe ) {
                return false;
            }
        }
    }
}
