package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.jsr303.util.SecureActions;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Description: <br/>
 * InvocationHandler implementation of <code>Annotation</code> that pretends it is a
 * "real" source code annotation.
 * <p/>
 * <p/>
 * User: roman <br/>
 * Date: 27.11.2009 <br/>
 * Time: 14:31:26 <br/>
 * Copyright: Agimatec GmbH
 */
public class AnnotationProxy implements Annotation, InvocationHandler {

    private final Class<? extends Annotation> annotationType;
    private final Map<String, Object> values;

    public AnnotationProxy(MetaAnnotation descriptor) {
        this.annotationType = descriptor.getType();
        values = getAnnotationValues(descriptor);
    }

    private Map<String, Object> getAnnotationValues(MetaAnnotation descriptor) {
        Map<String, Object> result = new HashMap();
        int processedValuesFromDescriptor = 0;
        final Method[] declaredMethods = SecureActions.getDeclaredMethods(annotationType);
        for (Method m : declaredMethods) {
            if (descriptor.contains(m.getName())) {
                result.put(m.getName(), descriptor.getValue(m.getName()));
                processedValuesFromDescriptor++;
            } else if (m.getDefaultValue() != null) {
                result.put(m.getName(), m.getDefaultValue());
            } else {
                throw new IllegalArgumentException("No value provided for " + m.getName());
            }
        }
        if (processedValuesFromDescriptor != descriptor.size()) {
            throw new RuntimeException(
                  "Trying to instanciate " + annotationType + " with unknown paramters.");
        }
        return result;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (values.containsKey(method.getName())) {
            return values.get(method.getName());
        }
        return method.invoke(this, args);
    }

    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('@').append(annotationType().getName()).append('(');
        boolean comma = false;
        for (String m : getMethodsSorted()) {
            if (comma) result.append(", ");
            result.append(m).append('=').append(values.get(m));
            comma = true;
        }
        result.append(")");
        return result.toString();
    }

    private SortedSet<String> getMethodsSorted() {
        SortedSet<String> result = new TreeSet();
        result.addAll(values.keySet());
        return result;
    }
}

