package com.agimatec.utility;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.*;
import java.util.Collection;
import java.beans.PropertyDescriptor;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;

/**
 * Description: Reflection Utils<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 11:42:50 <br/>
 * 
 */
public class ReflectUtils {
    public static Class getBeanTypeFromField(Field field) {
        return getBeanType(field.getGenericType());
    }

    public static Class getBeanTypeFromGetter(Method method) {
        return getBeanType(method.getGenericReturnType());
    }

    public static Class getBeanTypeFromSetter(Method method) {
        return getBeanType(method.getGenericParameterTypes()[0]);
    }

    public static Class getBeanType(final Class clazz, final String token) {
        return getBeanType(getGenericType(clazz, token));
    }

    private static Class getBeanType(Type t) {
        if (t instanceof GenericArrayType) {
            return getBeanClass(t);
        } else if (t instanceof ParameterizedType) {     // a list of what?
            ParameterizedType p = (ParameterizedType) t;
            Type rt = p.getRawType();
            if (rt instanceof Class && p.getActualTypeArguments().length > 0) {
                Class c = (Class) rt;
                if (Collection.class.isAssignableFrom(c))
                    return getBeanClass(p.getActualTypeArguments()[0]);
            }
            return getBeanClass(rt);
        } else {
            return getBeanClass(t);
        }
    }

    public static Type getGenericType(final Class clazz, final String token) {
        try {
            return clazz.getMethod("get" + StringUtils.capitalize(token))
                    .getGenericReturnType();
        } catch (NoSuchMethodException e) {// do nothing
        }
        try {
            return clazz.getField(token).getGenericType();
        } catch (NoSuchFieldException e1) { // do nothing
        }
        try {
            Method setter = clazz.getMethod("set" + StringUtils.capitalize(token));
            if (setter.getGenericParameterTypes().length == 1) {
                return setter.getGenericParameterTypes()[0];
            }
        } catch (NoSuchMethodException e2) {  // do nothing
        }
        try {
            return clazz.getDeclaredField(token).getGenericType();
        } catch (NoSuchFieldException e2) { // do nothing
        }
        final PropertyDescriptor propDesc =
                getPropertyDescriptor(clazz, Introspector.decapitalize(token));
        return (propDesc == null) ? null : propDesc.getPropertyType();
    }

    private static Class getBeanClass(Type t) {
        if (t instanceof Class) {
            Class c = (Class) t;
            if (c.isArray()) {
                return c.getComponentType();
            }
            return c;
        } else if (t instanceof ParameterizedType) {
            return getBeanClass(((ParameterizedType) t).getRawType());
        } else if (t instanceof GenericArrayType) {
            return getBeanClass(((GenericArrayType) t).getGenericComponentType());
        } else {
            return null;
        }
    }

    /**
     * @param theClass - javabean class
     * @param name     - javabean property name (already decapitalized)
     * @return javabean propertydescriptor or null
     */
    public static PropertyDescriptor getPropertyDescriptor(final Class theClass,
                                                           final String name) {
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(theClass);
            final PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            if (descriptors == null) return null;
            for (PropertyDescriptor descriptor : descriptors) {
                if (name.equals(descriptor.getName())) {
                    return descriptor;
                }
            }
            return null;
        } catch (IntrospectionException e) {
            return null;
        }
    }
}
