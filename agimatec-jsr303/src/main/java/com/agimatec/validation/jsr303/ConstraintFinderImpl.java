package com.agimatec.validation.jsr303;

import com.agimatec.validation.jsr303.groups.Group;
import com.agimatec.validation.jsr303.groups.Groups;
import com.agimatec.validation.jsr303.groups.GroupsComputer;

import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.Scope;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: TODO RSt - check spec and test this impl.<br/>
 * User: roman <br/>
 * Date: 13.10.2009 <br/>
 * Time: 10:44:46 <br/>
 * Copyright: Agimatec GmbH
 */
final class ConstraintFinderImpl implements ElementDescriptor.ConstraintFinder {
    private final Set<ConstraintDescriptor<?>> constraintDescriptors;

    ConstraintFinderImpl(Set<ConstraintDescriptor<?>> constraintDescriptors) {
        this.constraintDescriptors = constraintDescriptors;
    }

    public ElementDescriptor.ConstraintFinder unorderedAndMatchingGroups(
          Class<?>... groups) {
        Set<ConstraintDescriptor<?>> matchingDescriptors =
              new HashSet<ConstraintDescriptor<?>>(constraintDescriptors.size());
        Groups groupChain = new GroupsComputer().computeGroups(groups);
        for (Group group : groupChain.getGroups()) {
            for (ConstraintDescriptor<?> descriptor : constraintDescriptors) {
                if (descriptor.getGroups().contains(group.getGroup())) {
                    matchingDescriptors.add(descriptor);
                }
            }
        }
        return new ConstraintFinderImpl(matchingDescriptors);
    }

    public ElementDescriptor.ConstraintFinder lookingAt(Scope scope) {
        // TODO RSt - nyi
         return new ConstraintFinderImpl(Collections.EMPTY_SET);
    }

    public ElementDescriptor.ConstraintFinder declaredOn(ElementType... elementTypes) {
        Set<ConstraintDescriptor<?>> matchingDescriptors =
              new HashSet<ConstraintDescriptor<?>>(constraintDescriptors.size());
        for (ElementType each : elementTypes) {
            for (ConstraintDescriptor descriptor : constraintDescriptors) {
                switch (each) {
                    case FIELD:
                        if (((ConstraintValidation) descriptor).getField() != null) {
                            matchingDescriptors.add(descriptor);
                        }
                        break;
                    case METHOD:
                        if (((ConstraintValidation) descriptor).getField() == null) {
                            matchingDescriptors.add(descriptor);
                        }
                        break;
                }
            }
        }
        return new ConstraintFinderImpl(matchingDescriptors);
    }

    public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        return constraintDescriptors;
    }

    public boolean hasConstraints() {
        return !constraintDescriptors.isEmpty();
    }
}
