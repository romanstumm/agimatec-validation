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

import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import javax.validation.constraints.Pattern;

/**
 * validator using a regular expression,
 * based on the jsr303 Pattern constraint annotation.
 */
public class PatternValidator implements ConstraintValidator<Pattern, String> {
    private static final Log log = LogFactory.getLog(PatternValidator.class);

    protected java.util.regex.Pattern pattern;

    public void initialize(Pattern params) {
        try {
            pattern = java.util.regex.Pattern.compile(params.regexp(), toInt(params.flags()));
        } catch ( PatternSyntaxException e ) {
            throw new ValidationException( "Invalid regular expression pattern", e );
        }
    }

    private int toInt(Pattern.Flag[] flags) {
        int value = 0;
        for (Pattern.Flag flag : flags) {
            value |= toInt(flag);
        }
        return value;
    }

    private int toInt(Pattern.Flag flag) {
        switch (flag) {
            case UNIX_LINES:
                return java.util.regex.Pattern.UNIX_LINES;
            case CASE_INSENSITIVE:
                return java.util.regex.Pattern.CASE_INSENSITIVE;
            case COMMENTS:
                return java.util.regex.Pattern.COMMENTS;
            case MULTILINE:
                return java.util.regex.Pattern.MULTILINE;
            case DOTALL:
                return java.util.regex.Pattern.DOTALL;
            case UNICODE_CASE:
                return java.util.regex.Pattern.UNICODE_CASE;
            case CANON_EQ:
                return java.util.regex.Pattern.CANON_EQ;
            default:
                try {
                    return ((Integer) java.util.regex.Pattern.class.getField(flag.name())
                          .get(null)).intValue();
                } catch (Exception e) {
                    log.error("flag not supported: " + flag.name());
                    return 0;
                }
        }
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || pattern.matcher(value).matches();
    }
}
