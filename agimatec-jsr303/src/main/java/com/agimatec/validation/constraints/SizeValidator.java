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
import javax.validation.constraints.Size;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Description: Validate strings, arrays, maps and collections for Size constraint <br/>
 * User: roman <br/>
 * Date: 03.02.2009 <br/>
 * Time: 12:49:24 <br/>
 * Copyright: Agimatec GmbH
 */
public class SizeValidator implements ConstraintValidator<Size, Object> {
    private int min;
    private int max;

    /**
     * Configure the constraint validator based on the elements
     * specified at the time it was defined.
     *
     * @param constraint the constraint definition
     */
    public void initialize(Size constraint) {
        min = constraint.min();
        max = constraint.max();
        if (min < 0)
            throw new ValidationException( "Min cannot be negative" );
        if (max < 0)
            throw new ValidationException( "Max cannot be negative" );
        if (max < min)
            throw new ValidationException( "Max cannot be less than Min" );
    }

    /**
     * Validate a specified value.
     * returns false if the specified value does not conform to the definition
     *
     * @throws IllegalArgumentException if the object is not supported type
     */
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int size;
        if (value instanceof String) {
            size = ((String) value).length();
        } else if (value instanceof Collection) {
            size = ((Collection) value).size();
        } else if (value instanceof Map) {
            size = ((Map) value).size();
        } else if (value instanceof Array) {
            size = Array.getLength(value);
        } else {
            throw new UnexpectedTypeException(value + " is of unexpected type");
        }
        return size >= min && size <= max;
    }

}