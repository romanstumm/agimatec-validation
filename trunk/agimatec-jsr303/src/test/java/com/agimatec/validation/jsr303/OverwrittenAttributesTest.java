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

import junit.framework.TestCase;

/**
 * Description: TODO RSt - nyi <br/>
 * User: roman <br/>
 * Date: 05.10.2009 <br/>
 * Time: 10:02:12 <br/>
 * Copyright: Agimatec GmbH
 */
public class OverwrittenAttributesTest extends TestCase {
    /*
    @Pattern(regexp="[0-9]*")
@Size
@Constraint(validatedBy = FrenchZipcodeValidator.class)
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface FrenchZipcode {
    String message() default "Wrong zipcode";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @OverridesAttribute.List( {
        @OverridesAttribute(constraint=Size.class, name="min"),
        @OverridesAttribute(constraint=Size.class, name="max") } )
    int size() default 5;

    @OverridesAttribute(constraint=Size.class, name="message")
    String sizeMessage() default "{com.acme.constraint.FrenchZipcode.zipcode.size}";

    @OverridesAttribute(constraint=Pattern.class, name="message")
    String numberMessage() default "{com.acme.constraint.FrenchZipcode.number.size}";

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FrenchZipcode[] value();
    }
}
     */

// add a test for Example 2.11. Use of constraintIndex in @OverridesAttribute
    /*
    @Pattern.List( {
    @Pattern(regexp="[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}"), //email
    @Pattern(regexp=".*?emmanuel.*?") //emmanuel
} )
@Constraint(validatedBy={})
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface EmmanuelsEmail {
    String message() default "Not emmanuel's email";

    @OverridesAttribute(constraint=Pattern.class, name="message", constraintIndex=0)
    String emailMessage() default "Not an email";

    @OverridesAttribute(constraint=Pattern.class, name="message", constraintIndex=1)
    String emmanuelMessage() default "Not Emmanuel";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        EmmanuelsEmail[] value();
    }
}
     */
}
