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
import javax.validation.constraints.Past;
import java.util.Calendar;
import java.util.Date;

/**
 * Description: validate a date or calendar representing a date in the past<br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:49:16 <br/>
 * Copyright: Agimatec GmbH
 */
public class PastValidator implements ConstraintValidator<Past, Object> {

    public void initialize(Past constraintAnnotation) {
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (value instanceof Date) {
            return ((Date) value).before(now());
        } else if (value instanceof Calendar) {
            return ((Calendar) value).getTime().before(now());
        } else {
            return false;
        }
    }

    /**
     * overwrite when you need a different algorithm for 'now'.
     *
     * @return current date/time
     */
    protected Date now() {
        return new Date();
    }
}