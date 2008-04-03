package com.agimatec.utility.validation.jsr303;

import javax.validation.Constraint;
import javax.validation.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: MetaData class<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 12:26:50 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintDescriptorImpl implements ConstraintDescriptor {
    private Map<String, Object> parameters;
    private final Set<String> groups;
    private final ConstraintValidation constraintValidation;

    public ConstraintDescriptorImpl(ConstraintValidation constraintValidation) {
        this.constraintValidation = constraintValidation;
        if (constraintValidation.getGroups() != null) {
            groups = new HashSet(constraintValidation.getGroups().length);
            for (String eachGroup : constraintValidation.getGroups()) {
                groups.add(eachGroup);
            }
        } else {
            groups = Collections.EMPTY_SET;
        }
    }

    /** TODO RSt - what if the constraint is NOT based on a annotation? */
    public Annotation getAnnotation() {
        return constraintValidation.getAnnotation();
    }

    public boolean isFieldAccess() {
        return constraintValidation.getElement() instanceof Field;
    }

    public Map<String, Object> getParameters() {
        if (parameters == null) {
            parameters = new HashMap();
            if (getAnnotation() != null) {
                for (Method method : getAnnotation().annotationType().getDeclaredMethods()) {
                    if (method.getParameterTypes().length == 0) {
                        try {
                            parameters.put(method.getName(), method.invoke(getAnnotation()));
                        } catch (Exception e) { // do nothing
                        }
                    }
                }
            }
        }
        return parameters;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Constraint getConstraintImplementation() {
        return constraintValidation.getConstraint();
    }
}
