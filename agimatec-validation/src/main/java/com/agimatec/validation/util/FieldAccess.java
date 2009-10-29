package com.agimatec.validation.util;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;

/**
 * Description: direct field access.<br/>
 * User: roman <br/>
 * Date: 29.10.2009 <br/>
 * Time: 12:13:08 <br/>
 * Copyright: Agimatec GmbH
 */
public class FieldAccess extends AccessStrategy {

    private final Field field;

    public FieldAccess(Field field) {
        this.field = field;
        if(!field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public Object get(final Object instance) {
        return PrivilegedActions.run(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    return field.get(instance);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    public ElementType getElementType() {
        return ElementType.FIELD;
    }

    public String toString() {
        return "FieldAccess{" + "field=" + field + '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldAccess that = (FieldAccess) o;

        return field.equals(that.field);
    }

    public int hashCode() {
        return field.hashCode();
    }
}
