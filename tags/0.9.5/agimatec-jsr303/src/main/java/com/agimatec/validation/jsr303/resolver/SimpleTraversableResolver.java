package com.agimatec.validation.jsr303.resolver;

import javax.validation.Path;
import javax.validation.TraversableResolver;
import java.lang.annotation.ElementType;

/**
 * Description: traversable resolver that does always resolve<br/>
 * User: roman <br/>
 * Date: 25.11.2009 <br/>
 * Time: 13:21:18 <br/>
 * Copyright: Agimatec GmbH
 */
public class SimpleTraversableResolver implements TraversableResolver, CachingRelevant {
    /** @return true */
    public boolean isReachable(Object traversableObject, Path.Node traversableProperty,
                               Class<?> rootBeanType, Path pathToTraversableObject,
                               java.lang.annotation.ElementType elementType) {
        return true;
    }

    /** @return true */

    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty,
                                Class<?> rootBeanType, Path pathToTraversableObject,
                                ElementType elementType) {
        return true;
    }

    public boolean needsCaching() {
        return false;  // no
    }
}
