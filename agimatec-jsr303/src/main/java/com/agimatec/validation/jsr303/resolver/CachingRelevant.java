package com.agimatec.validation.jsr303.resolver;

/**
 * Description: indicator interface to let the implementation choose
 * whether results of traversable resolver should be cached <br/>
 * User: roman <br/>
 * Date: 25.11.2009 <br/>
 * Time: 13:59:20 <br/>
 * Copyright: Agimatec GmbH
 */
public interface CachingRelevant {
    boolean needsCaching();
}
