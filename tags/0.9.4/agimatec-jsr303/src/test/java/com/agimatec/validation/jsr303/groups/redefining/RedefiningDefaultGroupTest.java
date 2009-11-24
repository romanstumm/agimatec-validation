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
package com.agimatec.validation.jsr303.groups.redefining;

import com.agimatec.validation.jsr303.AgimatecValidatorFactory;
import com.agimatec.validation.jsr303.util.TestUtils;
import junit.framework.TestCase;

import javax.validation.ConstraintViolation;
import javax.validation.GroupDefinitionException;
import javax.validation.Validator;
import java.util.Set;

/**
 * Description: test Redefining the Default group for a class (spec. chapter 3.4.3)<br/>
 * User: roman <br/>
 * Date: 05.10.2009 <br/>
 * Time: 12:42:40 <br/>
 * Copyright: Agimatec GmbH
 */
public class RedefiningDefaultGroupTest extends TestCase {
    private Validator validator;

    protected void setUp() {
        validator = AgimatecValidatorFactory.getDefault().getValidator();
    }

    /**
     * when an address object is validated for the group Default,
     * all constraints belonging to the group Default and hosted on Address are evaluated
     */
    public void testValidateDefaultGroup() {
        Address address = new Address();
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertEquals(3, violations.size());
        assertNotNull(TestUtils.getViolation(violations, "street1"));
        assertNotNull(TestUtils.getViolation(violations, "zipCode"));
        assertNotNull(TestUtils.getViolation(violations, "city"));

        address.setStreet1("Elmstreet");
        address.setZipCode("1234");
        address.setCity("Gotham City");
        violations = validator.validate(address);
        assertTrue(violations.isEmpty());

        violations = validator.validate(address, Address.HighLevelCoherence.class);
        assertEquals(0, violations.size());

        address.setCity("error");
        violations = validator.validate(address, Address.HighLevelCoherence.class);
        assertEquals(1, violations.size());

        /**
         * If none fails, all HighLevelCoherence constraints present on Address are evaluated.
         *
         * In other words, when validating the Default group for Address,
         * the group sequence defined on the Address class is used.
         */
        violations = validator.validate(address);
        assertEquals(
              "redefined default group for Address must also validate HighLevelCoherence",
              1, violations.size());
    }

    public void testRaiseGroupDefinitionException() {
        InvalidRedefinedDefaultGroupAddress address =
              new InvalidRedefinedDefaultGroupAddress();
        try {
            validator.validate(address);
            fail();
        } catch (GroupDefinitionException ex) {

        }
    }
}
