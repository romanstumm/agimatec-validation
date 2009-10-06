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

import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.jsr303.groups.GroupsComputer;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.Validation;

import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: MetaData class<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 12:23:45 <br/>
 * Copyright: Agimatec GmbH 2008
 */
abstract class ElementDescriptorImpl implements ElementDescriptor {
    protected MetaBean metaBean;
    protected Class elementClass;
    private Set<ConstraintDescriptor<?>> constraintDescriptors;

    public ElementDescriptorImpl(MetaBean metaBean, Validation[] validations) {
        this.metaBean = metaBean;
        this.elementClass = metaBean.getBeanClass();
        createConstraintDescriptors(validations);
    }

    public ElementDescriptorImpl() {
    }

    /**
     * @return Statically defined returned type.
     */
    public Class getElementClass() {
        return elementClass;
    }

    public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        return constraintDescriptors;
    }

    /** return true if at least one constraint declaration is present on the element. */
    public boolean hasConstraints() {
        if (metaBean.getValidations().length > 0) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getValidations().length > 0) return true;
        }
        return false;
    }

    public Set<ConstraintDescriptor<?>> getUnorderedConstraintDescriptorsMatchingGroups(
          Class<?>... groups) {
        Set<ConstraintDescriptor<?>> matchingDescriptors =
              new HashSet<ConstraintDescriptor<?>>();
        Groups groupChain = new GroupsComputer().computeGroups(groups);
        for (Group group : groupChain.getGroups()) {
            for (ConstraintDescriptor<?> descriptor : constraintDescriptors) {
                if (descriptor.getGroups().contains(group.getGroup())) {
                    matchingDescriptors.add(descriptor);
                }
            }
        }
        return Collections.unmodifiableSet(matchingDescriptors);
    }

    protected void createConstraintDescriptors(Validation[] validations) {
        final Set<ConstraintDescriptor<?>> cds = new HashSet(validations.length);
        for (Validation validation : validations) {
            if (validation instanceof ConstraintValidation) {
                ConstraintValidation cval = (ConstraintValidation) validation;
                cds.add(cval);
            }
        }
        setConstraintDescriptors(cds);
    }

    public void setConstraintDescriptors(
          Set<ConstraintDescriptor<?>> constraintDescriptors) {
        this.constraintDescriptors = constraintDescriptors;
    }

    public void setElementClass(Class returnType) {
        this.elementClass = returnType;
    }

}
