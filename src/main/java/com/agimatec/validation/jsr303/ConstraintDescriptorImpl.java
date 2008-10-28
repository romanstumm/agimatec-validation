package com.agimatec.validation.jsr303;

import javax.validation.Constraint;
import javax.validation.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: MetaData class<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 12:26:50 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ConstraintDescriptorImpl implements ConstraintDescriptor {
    private Map<String, Object> parameters;
    private final Set<String> groups;
    private final ConstraintValidation constraintValidation;

    public ConstraintDescriptorImpl(ConstraintValidation constraintValidation) {
        this.constraintValidation = constraintValidation;
        if (constraintValidation.getGroups() != null) {
            groups = new HashSet(constraintValidation.getGroups().length);
            groups.addAll(Arrays.asList(constraintValidation.getGroups()));
        } else {
            groups = Collections.EMPTY_SET;
        }
    }

    /** TODO RSt - generate annotation when descriptor is based on XML */
    public Annotation getAnnotation() {
        return constraintValidation.getAnnotation();
    }

 /*   public boolean isFieldAccess() {
        return constraintValidation.getField() instanceof Field;
    }*/

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

    // TODO RSt - nyi
    public Set<ConstraintDescriptor> getComposingConstraints() {
        return null;  // do nothing
    }

    // TODO RSt - nyi
    public boolean isReportAsSingleInvalidConstraint() {
        return false;  // do nothing
    }

    public Set<String> getGroups() {
        return groups;
    }

    // TODO RSt - nyi
    public Class<? extends Constraint> getContstraintClass() {
        return constraintValidation.getConstraint().getClass();
    }

    /**
     * @deprecated not part of API anymore
     * @return
     */
    public Constraint getConstraintImplementation() {
        return constraintValidation.getConstraint();
    }
}
