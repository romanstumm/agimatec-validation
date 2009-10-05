package com.agimatec.validation.jsr303.util;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 05.10.2009 <br/>
 * Time: 12:00:35 <br/>
 * Copyright: Agimatec GmbH
 */
public class TestUtils {
    /**
     * @param violations
     * @param propertyPath - string format of a propertyPath
     * @return the constraintViolation with the propertyPath's string representation given
     */
    public static ConstraintViolation getViolation(Set violations, String propertyPath)
    {
        for(ConstraintViolation each : (Set<ConstraintViolation>)violations) {
            if(each.getPropertyPath().toString().equals(propertyPath)) return each;
        }
        return null;
    }
}
