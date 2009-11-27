/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.jsr303.ConstraintValidation;
import com.agimatec.validation.jsr303.util.TypeUtils;

import javax.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Description: hold parsed information from xml to complete MetaBean later<br/>
 * User: roman <br/>
 * Date: 27.11.2009 <br/>
 * Time: 13:53:40 <br/>
 * Copyright: Agimatec GmbH
 */
public class MetaConstraint<T, A extends Annotation> {

    /** The member the constraint was defined on. */
    private final Member member;

    /**
     * The JavaBeans name of the field/property the constraint was placed on. {@code null} if this is a
     * class level constraint.
     */
    private final String propertyName;

    /** The class of the bean hosting this constraint. */
    private final Class<T> beanClass;

    /** The constraint tree created from the constraint annotation. */
    private final ConstraintValidation<A> constraintValidation;

    /**
     * @param beanClass            The class in which the constraint is defined on
     * @param member               The member on which the constraint is defined on, {@code null} if it is a class constraint}
     * @param constraintValidation The constraint descriptor for this constraint
     */
    public MetaConstraint(Class<T> beanClass, Member member,
                          ConstraintValidation<A> constraintValidation) {
        this.member = member;
        if (this.member != null) {
            this.propertyName = TypeUtils.getPropertyName(member);
            if (member instanceof Method && propertyName ==
                  null) { // can happen if member is a Method which does not follow the bean convention
                throw new ValidationException(
                      "Annotated methods must follow the JavaBeans naming convention. " +
                            member.getName() + "() does not.");
            }
        } else {
            this.propertyName = null;
        }
        this.beanClass = beanClass;
        this.constraintValidation = constraintValidation;
    }

    /**
     * @return Returns the list of groups this constraint is part of. This might include the default group even when
     *         it is not explicitly specified, but part of the redefined default group list of the hosting bean.
     */
    public Set<Class<?>> getGroupList() {
        return constraintValidation.getGroups();
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }

    public Member getMember() {
        return member;
    }

    /**
     * @return The JavaBeans name of the field/property the constraint was placed on. {@code null} if this is a
     *         class level constraint.
     */
    public String getPropertyName() {
        return propertyName;
	}

}
