package com.agimatec.validation.util;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;

/**
 * Description: invoke a zero-argument method (getter)<br/>
 * User: roman <br/>
 * Date: 29.10.2009 <br/>
 * Time: 12:12:46 <br/>
 * Copyright: Agimatec GmbH
 */
public class MethodAccess extends AccessStrategy {
    private final Method method;

    public MethodAccess(Method method) {
        this.method = method;
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    public Object get(final Object instance) {
        return PrivilegedActions.run(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    return method.invoke(instance);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    public ElementType getElementType() {
        return ElementType.METHOD;
    }

    public String toString() {
        return "MethodAccess{" + "method=" + method + '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodAccess that = (MethodAccess) o;

        return method.equals(that.method);
    }

    public int hashCode() {
        return method.hashCode();
    }
}
