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
package com.agimatec.validation.jsr303;

/**
 * Description: TODO RSt - nyi<br/>
 * User: roman <br/>
 * Date: 05.10.2009 <br/>
 * Time: 10:56:12 <br/>
 * Copyright: Agimatec GmbH
 */
public class ConstraintValidatorContextFluentAPITest {
    /*
Example 2.14. Using the fluent API to build custom constraint violations

//default path
context.buildConstraintViolationWithTemplate( "this detail is wrong" )
            .addConstraintViolation();

//default path + "street"
context.buildConstraintViolationWithTemplate( "this detail is wrong" )
            .addNode( "street" )
            .addConstraintViolation();

//default path + "addresses["home"].country.name"
context.buildConstraintViolationWithTemplate( "this detail is wrong" )
            .addNode( "addresses" )
            .addNode( "country" )
                .inIterable().atKey( "home" )
            .addNode( "name" )
            .addConstraintViolation();
     */

// test:
// If disableDefaultConstraintViolation is called, no custom error is added
// (using the error builder) and if the constraint is not valid, 
// a ValidationException is raised.

/**
 * Check that a text is within the authorized syntax
 * Error messages are using either key:
 *  - com.acme.constraint.Syntax.unknown if no particular syntax is detected
 *  - com.acme.constraint.Syntax.unauthorized if the syntax is not allowed
 */
//public class FineGrainedSyntaxValidator implements ConstraintValidator<Syntax, String> {
//    private Set<Format> allowedFormats;
//
//    /**
//     * Configure the constraint validator based on the elements
//     * specified at the time it was defined.
//     * @param constraint the constraint definition
//     */
//    public void initialize(Syntax constraint) {
//        allowedFormats = new HashSet( Arrays.asList( constraint.value() ) );
//    }
//
//    /**
//     * Validate a specified value.
//     * returns false if the specified value does not conform to the definition
//     */
//    public boolean isValid(String value, ConstraintValidatorContext context) {
//        if ( value == null ) return true;
//        Set<Format> guessedFormats = guessFormat(value);
//
//        context.disableDefaultConstraintViolation();
//        if ( guessedFormats.size() == 0 ) {
//            String unknown = "{com.acme.constraint.Syntax.unknown}";
//            context.buildConstraintViolationWithTemplate(unknown)
//                       .addConstraintViolation();
//        }
//        else {
//            String unauthorized = "{com.acme.constraint.Syntax.unauthorized}";
//            context.buildConstraintViolationWithTemplate(unauthorized)
//                       .addConstraintViolation();
//        }
//
//        return allowedFormats.size() == 0
//            || (! Collections.disjoint( guessFormat(value), allowedFormats ) ));
//    }
//
//    Set<Format> guessFormats(String text) { ... }
//}
}
