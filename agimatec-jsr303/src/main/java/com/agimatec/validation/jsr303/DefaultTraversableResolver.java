/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.agimatec.validation.jsr303;

import java.lang.annotation.ElementType;

import javax.validation.TraversableResolver;


/**
 *
 * @see javax.validation.TraversableResolver
 *
 */
public class DefaultTraversableResolver implements TraversableResolver {

    private static final String PERSISTENCE_UTIL_CLASSNAME =
        "javax.persistence.PersistenceUtil";

    private static final String JPA_AWARE_TRAVERSABLE_RESOLVER_CLASSNAME =
        "com.agimatec.validation.jsr303.JPATraversableResolver";

    private TraversableResolver jpaTR;


    public DefaultTraversableResolver() {
        try {
            // see if a javax.persistence.PersistenceUtil is available
            @SuppressWarnings("unused")
            Class<?> pu = Class.forName(PERSISTENCE_UTIL_CLASSNAME);
            // now load our JPA aware version
            Class<?> tr = Class.forName(JPA_AWARE_TRAVERSABLE_RESOLVER_CLASSNAME);
            jpaTR = (TraversableResolver) tr.newInstance();
        } catch (Exception e) {
            // ignore
        }
    }

    public boolean isTraversable(Object traversableObject,
            String traversableProperty, Class<?> rootBeanType,
            String pathToTraversableObject, ElementType elementType) {
        return ((jpaTR == null) || jpaTR.isTraversable(traversableObject,
            traversableProperty, rootBeanType, pathToTraversableObject,
            elementType));
    }
}
