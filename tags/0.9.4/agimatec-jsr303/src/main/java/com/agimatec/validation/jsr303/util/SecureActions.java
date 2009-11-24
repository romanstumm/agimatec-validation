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
package com.agimatec.validation.jsr303.util;

import com.agimatec.validation.util.PrivilegedActions;

import javax.validation.ValidationException;

/**
 * Description: utility methods to perform actions with AccessController or without.<br/>
 * User: roman <br/>
 * Date: 01.10.2009 <br/>
 * Time: 16:44:09 <br/>
 * Copyright: Agimatec GmbH
 */
public class SecureActions extends PrivilegedActions {

    /**
     * create a new instance of the class using the default no-arg constructor.
     * perform newInstance() call with AccessController.doPrivileged() if possible.
     *
     * @param cls - the class (no interface, non-abstract, has accessible default no-arg-constructor)
     * @return a new instance
     */
    public static <T> T newInstance(final Class<T> cls) {
        return newInstance(cls, ValidationException.class);
    }

    public static <T> T newInstance(final Class<T> cls, final Class[] paramTypes,
                                    final Object[] values) {
        return newInstance(cls, ValidationException.class, paramTypes, values);
    }
}
