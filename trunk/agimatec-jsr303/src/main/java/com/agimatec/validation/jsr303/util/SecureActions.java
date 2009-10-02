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
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.10.2009 <br/>
 * Time: 16:44:09 <br/>
 * Copyright: Agimatec GmbH
 */
public class SecureActions {

    public static <T> T newInstance(final Class<T> cls) {
        return run(new PrivilegedAction<T>() {
            public T run() {
                try {
                    return cls.newInstance();
                } catch (InstantiationException e) {
                    throw new ValidationException("Cannot instantiate : " + cls, e);
                } catch (IllegalAccessException e) {
                    throw new ValidationException("Cannot instantiate " + cls, e);
                } catch (RuntimeException e) {
                    throw new ValidationException("Cannot instantiate " + cls, e);
                }
            }
        });
    }

    public static <T> T run(PrivilegedAction<T> action) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(action);
        } else {
            return action.run();
        }
    }
}
