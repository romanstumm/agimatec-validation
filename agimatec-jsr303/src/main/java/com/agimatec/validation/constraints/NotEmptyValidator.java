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
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/** Description:  Check the non emptyness of the element (array, collection, map, string) */
public class NotEmptyValidator implements ConstraintValidator<NotEmpty, Object> {
    public void initialize(NotEmpty constraintAnnotation) {
        // do nothing
    }

    // enhancement: extend to support any object that has a public isEmpty():boolean method?
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        } else if (value instanceof Collection) {
            return !((Collection) value).isEmpty();
        } else if (value instanceof Map) {
            return !((Map) value).isEmpty();
        } else if (value instanceof String) {
            return ((String) value).length() > 0;
        } else {
            throw new UnexpectedTypeException(value + " is of unsupported type");
        }
    }
}
