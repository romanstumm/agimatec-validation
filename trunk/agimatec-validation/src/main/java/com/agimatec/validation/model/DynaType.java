package com.agimatec.validation.model;

import java.lang.reflect.Type;

/**
 * Description: implementation of a dynamic type. can be used inside a
 * MetaProperty for instance-based types <br/>
 * User: roman <br/>
 * Date: 12.02.2009 <br/>
 * Time: 16:49:56 <br/>
 * Copyright: Agimatec GmbH
 */
public interface DynaType extends Type {
    Type getRawType();
}
