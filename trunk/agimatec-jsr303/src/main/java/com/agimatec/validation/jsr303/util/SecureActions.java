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

import javax.validation.ValidationException;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Description: utility methods to perform actions with AccessController or without.<br/>
 * User: roman <br/>
 * Date: 01.10.2009 <br/>
 * Time: 16:44:09 <br/>
 * Copyright: Agimatec GmbH
 */
public class SecureActions {

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

    /**
     * @param cls - the type to create a new instance from
     * @param exception - type of exception to throw when newInstance() call fails
     * @return the new instance of 'cls'
     */
    public static <T, E extends RuntimeException> T newInstance(final Class<T> cls,
                                                                final Class<E> exception) {
        return run(new PrivilegedAction<T>() {
            public T run() {
                try {
                    return cls.newInstance();
                } catch (Exception e) {
                    throw newException("Cannot instantiate : " + cls, e);
                }
            }

            private RuntimeException newException(String msg, Throwable e) {
                try {
                    Constructor<E> co =
                          exception.getConstructor(String.class, Throwable.class);
                    try {
                        return co.newInstance(msg, e);
                    } catch (Exception e1) {
                        //noinspection ThrowableInstanceNeverThrown
                        return new RuntimeException(msg, e); // fallback
                    }
                } catch (NoSuchMethodException e1) {
                    //noinspection ThrowableInstanceNeverThrown
                    return new RuntimeException(msg, e); // fallback
                }
            }
        });
    }

    /**
     * perform action with AccessController.doPrivileged() if possible.
     *
     * @param action - the action to run
     * @return result of running the action
     */
    public static <T> T run(PrivilegedAction<T> action) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(action);
        } else {
            return action.run();
        }
    }
}
