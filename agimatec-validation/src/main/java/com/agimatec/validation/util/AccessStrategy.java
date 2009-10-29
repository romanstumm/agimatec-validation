package com.agimatec.validation.util;

import java.lang.annotation.ElementType;

/**
 * Description: abstract class to encapsulate different strategies
 * to get the value of a Property.<br/>
 * User: roman <br/>
 * Date: 29.10.2009 <br/>
 * Time: 12:12:08 <br/>
 * Copyright: Agimatec GmbH
 */
public abstract class AccessStrategy {
    /**
     * get the value from the given instance.
     * @param instance
     * @return the value
     * @throws IllegalArgumentException in case of an error
     */
    public abstract Object get(Object instance);

    public abstract ElementType getElementType(); 
}
