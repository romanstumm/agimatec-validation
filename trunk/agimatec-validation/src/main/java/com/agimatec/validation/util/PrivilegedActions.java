package com.agimatec.validation.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Description: utility methods to perform actions with AccessController or without. <br/>
 * User: roman <br/>
 * Date: 29.10.2009 <br/>
 * Time: 11:57:36 <br/>
 * Copyright: Agimatec GmbH
 */
public class PrivilegedActions {
    /**
     * create a new instance.
     *
     * @param cls - the class (no interface, non-abstract, has accessible default no-arg-constructor)
     * @return a new instance
     * @throws IllegalArgumentException on any error to wrap target exceptions.
     */
    public static <T> T newInstance(final Class<T> cls) {
        return newInstance(cls, IllegalArgumentException.class);
    }

    /**
     * create a new instance of the class using the default no-arg constructor.
     * perform newInstance() call with AccessController.doPrivileged() if possible.
     *
     * @param cls       - the type to create a new instance from
     * @param exception - type of exception to throw when newInstance() call fails
     * @return the new instance of 'cls'
     */
    public static <T, E extends RuntimeException> T newInstance(final Class<T> cls,
                                                                final Class<E> exception) {
        return run(new PrivilegedAction<T>() {
            public T run() {
                try {
                    return cls.newInstance();
                } catch (Exception e) {
                    throw newException("Cannot instantiate : " + cls, e);
                }
            }

            private RuntimeException newException(String msg, Throwable e) {
                try {
                    Constructor<E> co =
                          exception.getConstructor(String.class, Throwable.class);
                    try {
                        return co.newInstance(msg, e);
                    } catch (Exception e1) {
                        //noinspection ThrowableInstanceNeverThrown
                        return new RuntimeException(msg, e); // fallback
                    }
                } catch (NoSuchMethodException e1) {
                    //noinspection ThrowableInstanceNeverThrown
                    return new RuntimeException(msg, e); // fallback
                }
            }
        });
    }

    /**
     * perform action with AccessController.doPrivileged() if possible.
     *
     * @param action - the action to run
     * @return result of running the action
     */
    public static <T> T run(PrivilegedAction<T> action) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(action);
        } else {
            return action.run();
        }
    }

    public static Object getAnnotationValue(final Annotation annotation,
                                            final String name)
          throws IllegalAccessException, InvocationTargetException {
        return run(new PrivilegedAction() {
            public Object run() {
                Method valueMethod = null;
                try {
                    valueMethod = annotation.annotationType().getDeclaredMethod(name);
                } catch (NoSuchMethodException ex) { /* do nothing */ }
                if (null != valueMethod) {
                    try {
                        return valueMethod.invoke(annotation);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        });
    }
}
