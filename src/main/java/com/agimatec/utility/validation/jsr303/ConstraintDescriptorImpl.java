package com.agimatec.utility.validation.jsr303;

import javax.validation.Constraint;
import javax.validation.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

/**
 * Description: MetaData class<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 12:26:50 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ConstraintDescriptorImpl implements ConstraintDescriptor {
    private Constraint constraint;
    private Annotation annotation;
    private Map<String, Object> parameters;
    private Set<String> groups = Collections.EMPTY_SET;

    /**
     * TODO RSt - what if the constraint is NOT based on a annotation?
     * @return
     */
    public Annotation getAnnotation() {
        return annotation;
    }

    public Map<String, Object> getParameters() {
        if (parameters == null) {
            parameters = new HashMap();
            if (annotation != null) {
                for (Method method : annotation.annotationType().getDeclaredMethods()) {
                    if (method.getParameterTypes().length == 0) {
                        try {
                            parameters.put(method.getName(), method.invoke(annotation));
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
        return constraint;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

}
