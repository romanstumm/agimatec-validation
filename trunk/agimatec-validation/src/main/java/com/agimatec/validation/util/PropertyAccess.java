package com.agimatec.validation.util;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

/**
 * Description: Undefined dynamic strategy. Uses PropertyUtils or tries to determine
 * field to access the value<br/>
 * User: roman <br/>
 * Date: 29.10.2009 <br/>
 * Time: 12:27:27 <br/>
 * Copyright: Agimatec GmbH
 */
public class PropertyAccess extends AccessStrategy {
    private final Class beanClass;
    private final String propertyName;
    private Field rememberField;

    public PropertyAccess(Class clazz, String propertyName) {
        this.beanClass = clazz;
        this.propertyName = propertyName;
    }

    public ElementType getElementType() {
        return ElementType.METHOD;
    }

    public Object get(Object bean) {
        try {
            if (rememberField != null) {  // cache field of previous access
                return rememberField.get(bean);
            }

            try {   // try public method
                return PropertyUtils
                      .getSimpleProperty(bean, propertyName);
            } catch (NoSuchMethodException ex) {
                Object value;
                try { // try public field
                    Field aField = bean.getClass().getField(propertyName);
                    value = aField.get(bean);
                    rememberField = aField;
                    return value;
                } catch (NoSuchFieldException ex2) {
                    // search for private/protected field up the hierarchy
                    Class theClass = bean.getClass();
                    while (theClass != null) {
                        try {
                            Field aField = theClass
                                  .getDeclaredField(propertyName);
                            if (!aField.isAccessible()) {
                                aField.setAccessible(true);
                            }
                            value = aField.get(bean);
                            rememberField = aField;
                            return value;
                        } catch (NoSuchFieldException ex3) {
                            // do nothing
                        }
                        theClass = theClass.getSuperclass();
                    }
                    throw new IllegalArgumentException(
                          "cannot access field " + propertyName);
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot access " + propertyName, e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyAccess that = (PropertyAccess) o;

        return beanClass.equals(that.beanClass) && propertyName.equals(that.propertyName);
    }

    public int hashCode() {
        int result;
        result = beanClass.hashCode();
        result = 31 * result + propertyName.hashCode();
        return result;
    }
}
